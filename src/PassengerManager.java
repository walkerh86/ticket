import java.io.IOException;
import java.util.ArrayList;
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
import net.HttpResponseHandler;
import net.MyHttpResponse;
import net.StringHttpResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class PassengerManager  implements HttpResponseHandler{
	private static final String KEY_PASSENGER_NAME = "passenger_name";
	private static final String KEY_PASSENGER_IDNO = "passenger_id_no";
	private static final String KEY_PASSENGER_TYPE = "passenger_type";
	
	private Object mLock = new Object();
	private RequestProcess mRequestProcess;
	private ArrayList<Passenger> mRemotePassengers = new ArrayList<Passenger>(20);
	private ArrayList<Passenger> mLocalPassengers = new ArrayList<Passenger>(20);
	private ArrayList<Passenger> mSelectedPassengers = new ArrayList<Passenger>(20);
	private int mTotalPage;
	private int mPageSize;
	private int mCurrQueryPageIdx;
	
	public PassengerManager(RequestProcess process){
		mRequestProcess = process;
	}
	
	public void initPassengers(){
		mRequestProcess.initPassengersRequest(this);
	}
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){	
			if(response.mStep == RequestProcess.STEP_INIT_PASSENGERS){
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					parseInitResponse(strResponse.mResult);
				}
			}else if(response.mStep == RequestProcess.STEP_QUERY_PASSENGERS){
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					//parsePassengerFromHtml(strResponse.mResult);
					parsePassengerFromJson(strResponse.mResult);
					if(mCurrQueryPageIdx >= mTotalPage){
						finishQueryPassengers();
					}else{
						mCurrQueryPageIdx++;
						mRequestProcess.queryPassengersRequest(this,mCurrQueryPageIdx,mPageSize);
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
			mRequestProcess.queryPassengersRequest(this,mCurrQueryPageIdx,mPageSize);
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
			//Log.i(jPassenger.toString());
			Passenger passenger = new Passenger();
			passenger.mName = jPassenger.getString(KEY_PASSENGER_NAME);
			passenger.mIdNo = jPassenger.getString(KEY_PASSENGER_IDNO);
			passenger.mType = jPassenger.getString(KEY_PASSENGER_TYPE);
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
	
	public class Passenger{
		public String mName;
		public String mIdNo;
		public String mType;
		
		public String toString(){
			return mName+"|"+mIdNo+"|"+mType;
		}
	}
}
