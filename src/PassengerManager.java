import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.Log;
import util.UrlConstants;
import net.HttpHeader;
import net.HttpResponseHandler;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StringHttpResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class PassengerManager  implements HttpResponseHandler{
	private static final String KEY_PASSENGER_NAME = "passenger_name";
	private static final String KEY_PASSENGER_IDNO = "passenger_id_no";
	private static final String KEY_PASSENGER_TYPE_NAME = "passenger_type_name";
	private static final String KEY_PASSENGER_ID_TYPE_NAME = "passenger_type_name";
	private static final String KEY_PASSENGER_TYPE_CODE = "passenger_type";
	private static final String KEY_PASSENGER_ID_TYPE_CODE = "passenger_id_type_code";
	
	private static final String KEY_PASSENGERS = "passengers";
	private static final String PASSENGER_PROPERTY_URL = "passengers";
	
	public static final int STEP_INIT_PASSENGERS = 20;
	public static final int STEP_QUERY_PASSENGERS = 21;
	
	public static final int PASSENGERS_MAX = 20;
	
	private Object mLock = new Object();
	private LinkedHashMap<String,Passenger> mRemotePassengers = new LinkedHashMap<String,Passenger>(PASSENGERS_MAX);
	private LinkedHashMap<String,Passenger> mLocalPassengers = new LinkedHashMap<String,Passenger>(PASSENGERS_MAX);
	private LinkedHashMap<String,Passenger> mSelectedPassengers = new LinkedHashMap<String,Passenger>(PASSENGERS_MAX);
	private int mTotalPage;
	private int mPageSize;
	private int mCurrQueryPageIdx;
	
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	
	private OnPassengersGetDoneListener mOnPassengersGetDoneListener;
	
	private static PassengerManager mPassengerManager;
	private boolean mRemotePassengerGetDone;
	
	private Properties mProperties = new Properties();
	
	public PassengerManager(){
	}
	
	public static PassengerManager getInstance(){
		if(mPassengerManager == null){
			mPassengerManager = new PassengerManager();
		}
		return mPassengerManager;
	}
	
	public void setRequestQueue(BlockingQueue<MyHttpUrlRequest> queue){
		mRequestQueue = queue;
	}
	
	public void initPassengers(OnPassengersGetDoneListener listener){
		mOnPassengersGetDoneListener = listener;
		mRemotePassengers.clear();
		if(mRemotePassengerGetDone){
			finishQueryPassengers();
		}else{
			initPassengersRequest(this);
		}
		mLocalPassengers.clear();
		loadLocalPassengers();
	}
	
	private void loadLocalPassengers(){
		try {
			InputStream is = new FileInputStream(new File(PASSENGER_PROPERTY_URL));
					
			mProperties.load(is);
			String value = mProperties.getProperty(KEY_PASSENGERS);
			if(value != null){
				String[] passengerStrs = value.split("[,;]");
				for(int i=0;i<passengerStrs.length;i++){
					Passenger passenger = Passenger.fromString(passengerStrs[i]);
					addLocalPassenger(passenger);
				}
				
				if(mOnPassengersGetDoneListener != null){
					mOnPassengersGetDoneListener.OnLocalPassengersGetDone(mLocalPassengers);
				}
			}
		}catch (IOException e) {
			Log.i("loadLocalPassengers,e="+e);
		}
	}
	
	private void saveLocalPassengers(){
		mProperties.setProperty(KEY_PASSENGERS, getPassengersString(mLocalPassengers));

		try {
			FileOutputStream out = new FileOutputStream(new File(PASSENGER_PROPERTY_URL));					
			Log.i(mProperties.toString());
			mProperties.store(out, null);
		} catch (FileNotFoundException e) {
			Log.i("saveUserInfo,e=" + e);
		} catch (IOException e) {
			Log.i("saveUserInfo,e=" + e);
		}
	}
	
	public void initPassengersRequest(HttpResponseHandler handler){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_PASSENGERS_INIT_URL,"GET",
				HttpHeader.initPassengers(),null,
				new StringHttpResponse(handler,STEP_INIT_PASSENGERS)));
	}
	
	public void queryPassengersRequest(HttpResponseHandler handler, int pageIdex, int pageSize){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("pageIndex",Integer.toString(pageIdex));
		params.put("pageSize",Integer.toString(pageSize));
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_PASSENGERS_QUERY_URL,"POST",
				HttpHeader.initPassengers(),params,
				new StringHttpResponse(handler,STEP_QUERY_PASSENGERS)));
	}
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){	
			if(response.mStep == STEP_INIT_PASSENGERS){
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					parseInitResponse(strResponse.mResult);
				}
			}else if(response.mStep == STEP_QUERY_PASSENGERS){
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					//parsePassengerFromHtml(strResponse.mResult);
					parsePassengerFromJson(strResponse.mResult);
					if(mCurrQueryPageIdx >= mTotalPage){
						finishQueryPassengers();
					}else{
						mCurrQueryPageIdx++;
						queryPassengersRequest(this,mCurrQueryPageIdx,mPageSize);
					}
				}
			}
		}
	}
	
	private void parseInitResponse(String html){
		String regEx = ".*totlePage\\s=\\s(\\d*);";
		Pattern p=Pattern.compile(regEx);
		Matcher m=p.matcher(html);
		if(m.find()){
			Log.i("groupCount()="+m.groupCount());
			mTotalPage = Integer.valueOf(m.group(1));
		}
		regEx = ".*pageSize\\s=\\s(\\d*);";
		p=Pattern.compile(regEx);
		m=p.matcher(html);
		Log.i("groupCount()="+m.groupCount());
		if(m.find()){
			Log.i("groupCount()="+m.groupCount());
			mPageSize = Integer.valueOf(m.group(1));
		}
		Log.i("parseInitResponse,mTotalPage="+mTotalPage+",mPageSize="+mPageSize);
		parsePassengerFromHtml(html);
		
		if(mTotalPage > 1){
			mCurrQueryPageIdx = 2;
			queryPassengersRequest(this,mCurrQueryPageIdx,mPageSize);
		}else{
			finishQueryPassengers();
		}
	}	
	
	private void parsePassengerFromHtml(String html){
		String regEx = ".*passengers=(.*);";
		Pattern p=Pattern.compile(regEx);
		Matcher m=p.matcher(html);
		if(m.find()){
			//Log.i("groupCount()="+m.groupCount());
			//Log.i("groupCount(),result="+m.group(0));
			//Log.i("groupCount(),result="+m.group(1));
			JSONArray passengerList = JSONArray.fromObject(m.group(1));
			parsepassengerFromJsonArray(passengerList);
		}
	}
	
	private void parsepassengerFromJsonArray(JSONArray passengerList){
		int count = passengerList.size();
		for(int i=0;i<count;i++){
			JSONObject jPassenger = passengerList.getJSONObject(i);
			Log.i(jPassenger.toString());
			Passenger passenger = new Passenger();
			passenger.setName(jPassenger.getString(KEY_PASSENGER_NAME));
			passenger.setIdNo(jPassenger.getString(KEY_PASSENGER_IDNO));
			//passenger.mTypeName = jPassenger.getString(KEY_PASSENGER_TYPE_NAME);
			passenger.setTypeCode(jPassenger.getString(KEY_PASSENGER_TYPE_CODE));
			//passenger.mIdTypeName = jPassenger.getString(KEY_PASSENGER_ID_TYPE_NAME);
			passenger.setIdTypeCode(jPassenger.getString(KEY_PASSENGER_ID_TYPE_CODE));
			mRemotePassengers.put(passenger.getIdNo(),passenger);
		}
	}
	
	private void parsePassengerFromJson(String jsonStr){
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		//Log.i("jsonStr="+jsonObj.toString());
		JSONObject j1 = jsonObj.getJSONObject("data");
		//Log.i(j1.toString());
		JSONArray jPassengerArray = j1.getJSONArray("datas");
		parsepassengerFromJsonArray(jPassengerArray);
	}
	
	private void finishQueryPassengers(){
		mRemotePassengerGetDone = true;
		if(mOnPassengersGetDoneListener != null){
			mOnPassengersGetDoneListener.OnRemotePassengersGetDone(mRemotePassengers);
		}
		/*
		int count = mPassengers.size();
		Log.i("finishQueryPassengers,count="+count);
		for(int i=0;i<count;i++){
			Passenger passenger = mPassengers.get(i);
			Log.i("passenger, name="+passenger.mName+",idno="+passenger.mIdNo);
		}
		*/
	}	
	
	public void addLocalPassenger(Passenger passenger){
		if(!mLocalPassengers.containsKey(passenger.getIdNo())){
			mLocalPassengers.put(passenger.getIdNo(), passenger);
			saveLocalPassengers();
		}
	}
	
	public void addRemotePassenger(Passenger passenger){
		if(!mRemotePassengers.containsKey(passenger.getIdNo())){
			mRemotePassengers.put(passenger.getIdNo(), passenger);
		}
	}
	
	public Passenger getPassenger(String key){
		if(mLocalPassengers.containsKey(key)){
			return mLocalPassengers.get(key);
		}else if(mRemotePassengers.containsKey(key)){
			return mRemotePassengers.get(key);
		}
		return null;
	}
	
	public void addSelectPassenger(Passenger passenger){
		if(!mLocalPassengers.containsKey(passenger.getIdNo())){
			mLocalPassengers.put(passenger.getIdNo(), passenger);
		}
		selectSinglePassenger(passenger.getIdNo());
	}
	
	public void unSelectSinglePassenger(String key){
		if(mSelectedPassengers.containsKey(key)){
			mSelectedPassengers.remove(key);
		}
	}
	
	public void selectSinglePassenger(String key){
		if(mRemotePassengers.containsKey(key)){
			mSelectedPassengers.put(key, mRemotePassengers.get(key));
		}else if(mLocalPassengers.containsKey(key)){
			mSelectedPassengers.put(key, mLocalPassengers.get(key));
		}else{
			
		}
	}
	
	public void selectMultiPassengers(String passengers){
		if(passengers == null || passengers.length() == 0){
			return;
		}
		String[] strs = passengers.split("[,]");
		for(int i =0;i<strs.length;i++){
			Passenger passenger = Passenger.fromString(strs[i]);
			mSelectedPassengers.put(passenger.getIdNo(),passenger);
			Log.i("selectMultiPassengers,name="+passenger.getName());
		}
	}
		
	public HashMap<String,Passenger> getSelectedPassengers(){
		return mSelectedPassengers;
	}
	
	public HashMap<String,Passenger> getRemotePassengers(){
		return mRemotePassengers;
	}
	
	public String getSelectedPassengersString(){
		String result = "";
		if(mSelectedPassengers.size() == 0){
			return null;
		}
		for(Map.Entry<String, Passenger> entry : mSelectedPassengers.entrySet()){
			if(result.length() > 0){
				result += ",";
			}
			result += entry.getValue().toString();
		}
		Log.i("getSelectedPassengersString,result="+result);
		return result;
	}
	
	private static String getPassengersString(HashMap<String,Passenger> passengers){
		String result = "";
		if(passengers.size() == 0){
			return null;
		}
		for(Map.Entry<String, Passenger> entry : passengers.entrySet()){
			if(result.length() > 0){
				result += ",";
			}
			result += entry.getValue().toString();
		}
		Log.i("getPassengersString,result="+result);
		return result;
	}
	
	public interface OnPassengersGetDoneListener{
		public void OnRemotePassengersGetDone(HashMap<String,Passenger> passengers);
		public void OnLocalPassengersGetDone(HashMap<String,Passenger> passengers);
	}
}
