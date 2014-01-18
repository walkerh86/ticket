import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	
	public static final int STEP_INIT_PASSENGERS = 20;
	public static final int STEP_QUERY_PASSENGERS = 21;
	
	private Object mLock = new Object();
	private ArrayList<Passenger> mRemotePassengers = new ArrayList<Passenger>(20);
	private ArrayList<Passenger> mLocalPassengers = new ArrayList<Passenger>(20);
	private ArrayList<Passenger> mSelectedPassengers = new ArrayList<Passenger>(20);
	private int mTotalPage;
	private int mPageSize;
	private int mCurrQueryPageIdx;
	
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	
	public PassengerManager(BlockingQueue<MyHttpUrlRequest> queue){
		mRequestQueue = queue;
	}
	
	public void initPassengers(){
		//mRequestProcess.initPassengersRequest(this);
		//test
		Passenger passenger = new Passenger();
		passenger.setName("ºú¼Ì»ª");
		passenger.setIdNo("413028195612085729");
		mSelectedPassengers.add(passenger);
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
			mRemotePassengers.add(passenger);
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
		/*
		int count = mPassengers.size();
		Log.i("finishQueryPassengers,count="+count);
		for(int i=0;i<count;i++){
			Passenger passenger = mPassengers.get(i);
			Log.i("passenger, name="+passenger.mName+",idno="+passenger.mIdNo);
		}
		*/
	}	
	
	public ArrayList<Passenger> getSelectedPassengers(){
		return mSelectedPassengers;
	}
}
