import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;

import util.Log;
import util.TextUtil;


public class UserInfo {
	private static final String KEY_USER_NAME="user_name";
	private static final String KEY_USER_PW="user_pw";
	private static final String KEY_FROM_STATION_NAME="from_station_name";
	private static final String KEY_TO_STATION_NAME="to_station_name";
	private static final String KEY_FROM_STATION_CODE="from_station_code";
	private static final String KEY_TO_STATION_CODE="to_station_code";
	private static final String KEY_DATE="date";
	private static final String KEY_SEAT_FILTER="seat_filter";
	private static final String KEY_TRAIN_FILTER="train_filter";
	private static final String KEY_TRAIN_TYPE_FILTER="train_type_filter";
	private static final String KEY_PRIORITY_TYPE="priority_type";
	private static final String KEY_AUTO_QUERY="auto_query";
	private static final String KEY_QUERY_MODE="query_mode";
	private static final String KEY_SUBMIT_WITHOUT_ENOUGH_TICKET="submit_without_enough_ticket";
	public static final String KEY_PASSENGERS = "passengers";
	
	
	public static final String QUERY_MODE_QIANG = "qiang";
	public static final String QUERY_MODE_JIAN = "jian";
	
	public static final String PRIORITY_TYPE_NONE = "none";
	public static final String PRIORITY_TYPE_SEAT = "seat";
	public static final String PRIORITY_TYPE_TRAIN = "train";
	
	public static final String TRAIN_TYPE_FILTER_ALL = "ALL";
	public static final String TRAIN_TYPE_FILTER_G = "G";
	public static final String TRAIN_TYPE_FILTER_D = "D";
	public static final String TRAIN_TYPE_FILTER_Z = "Z";
	public static final String TRAIN_TYPE_FILTER_T = "T";
	public static final String TRAIN_TYPE_FILTER_K = "K";
	public static final String TRAIN_TYPE_FILTER_Q = "Q";
	
	private static final String USER_PROPERTY_URL="config.properties";
	//user info
	private String mUserName;
	private String mUserPw;
	private String mCaptchaCode;
	//query info
	private String mFromStationName;
	private String mToStationName;
	private String mFromStationCode;
	private String mToStationCode;
	private String mDate;
	private String mSeatTypeCode;
	//filter info	
	private String mSeatFilter="";
	private String mTrainFilter="";	
	private String mTrainTypeFilter="";
	//
	private String mPriorityType = PRIORITY_TYPE_NONE;
	private String mQueryAuto = "false";
	private String mQueryMode = QUERY_MODE_QIANG;
	private String mSubmitWithoutEnough = "false";
	//passenger
	private String mPassengers;
	//
	private Properties mProperties = new Properties();
	
	public UserInfo(){
		loadUserInfo();
	}
	
	public String getUserName(){
		return mUserName;
	}
	
	public String getUserPw(){
		return mUserPw;
	}
	
	public String getCaptchaCode(){
		return mCaptchaCode;
	}
	
	public void setUserName(String name){
		mUserName = name;
	}
	
	public void setUserPw(String pw){
		mUserPw = pw;
	}
	
	public void setCaptchaCode(String code){
		mCaptchaCode = code;
	}
	
	public void setFromStationName(String name){
		mFromStationName = name;
	}
	
	public String getFromStationName(){
		return mFromStationName;
	}
	
	public void setFromStationCode(String code){
		mFromStationCode = code;
	}
	
	public String getToStationName(){
		return mToStationName;
	}
	
	public void setToStationName(String name){
		mToStationName = name;
	}
	
	public String getFromStationCode(){
		return mFromStationCode;
	}
	
	public void setToStationCode(String code){
		mToStationCode = code;
		
	}
	
	public String getToStationCode(){
		return mToStationCode;
	}
	
	public void setDate(String date){
		mDate = date;
	}
	
	public String getDate(){
		return mDate;
	}
	
	public void setSeatTypeCode(String seatTypeCode){
		mSeatTypeCode = seatTypeCode;
	}
	
	public String getSeatTypeCode(){
		return mSeatTypeCode;
	}
	
	public String[] getSeatFitlerArray(){
		if(TextUtil.isEmpty(mSeatFilter)){
			return null;
		}
		String[] filters = mSeatFilter.split("[,;]");
		return filters;
	}
	
	public String getSeatFitler(){
		return mSeatFilter;
	}
		
	public void setSeatFilter(String filter){
		mSeatFilter = filter;
	}
	
	public void setSeatFilterByArray(String[] filters){
		if(filters == null || filters.length == 0){
			mSeatFilter = "";
		}else{
			mSeatFilter = TextUtil.getString(filters);
		}
	}
		
	public String[] getTrainFitlerArray(){
		if(TextUtil.isEmpty(mTrainFilter)){
			return null;
		}
		String[] filters = mTrainFilter.split("[,;]");
		return filters;
	}
	
	public String getTrainFilter(){
		return mTrainFilter;
	}
		
	public void setTrainFilter(String filter){
		mTrainFilter = filter;
	}
	
	public void setTrainFilterByArray(String[] filters){
		if(filters == null || filters.length == 0){
			mTrainFilter = "";
		}else{
			mTrainFilter = TextUtil.getString(filters);
		}
	}
	
	public String getPriorityType(){
		return mPriorityType;
	}
	
	public void setPriorityType(String type){
		mPriorityType = type;
	}
	
	public String getPassengers(){
		return mPassengers;
	}
	
	public void setPassengers(String passengers){
		mPassengers = passengers;
	}
	
	public String getTrainTypeFilter(){
		return mTrainTypeFilter;
	}
	
	public void setTrainTypeFilter(String filter){
		mTrainTypeFilter = filter;
	}
	
	public String getQueryAuto(){
		return mQueryAuto;
	}
	
	public void setQueryAuto(String auto){
		mQueryAuto = auto;
	}
	
	public String getQueryMode(){
		return mQueryMode;
	}
	
	public void setQueryMode(String mode){
		mQueryMode = mode;
	}
	
	public String getSubmitWithoutEnoughTicket(){
		return mSubmitWithoutEnough;
	}
	
	public void setSubmitWithoutEnoughTicket(String submit){
		mSubmitWithoutEnough = submit;
	}
	
	public void loadUserInfo(){
		try {
			InputStream is = new FileInputStream(new File(USER_PROPERTY_URL));
					
			mProperties.load(is);
			String value = mProperties.getProperty(KEY_USER_NAME);
			if(value != null){
				setUserName(value);
			}
			value = mProperties.getProperty(KEY_USER_PW);
			if(value != null){
				setUserPw(value);
			}
			value = mProperties.getProperty(KEY_FROM_STATION_CODE);
			if(value != null){
				setFromStationCode(value);
			}
			value = mProperties.getProperty(KEY_FROM_STATION_NAME);
			if(value != null){
				setFromStationName(value);
			}
			value = mProperties.getProperty(KEY_TO_STATION_CODE);
			if(value != null){
				setToStationCode(value);
			}
			value = mProperties.getProperty(KEY_TO_STATION_NAME);
			if(value != null){
				setToStationName(value);
			}
			value = mProperties.getProperty(KEY_DATE);
			if(value != null){
				setDate(value);
			}			
			value = mProperties.getProperty(KEY_SEAT_FILTER);
			if(value != null){
				setSeatFilter(value);
			}
			value = mProperties.getProperty(KEY_TRAIN_FILTER);
			if(value != null){
				setTrainFilter(value);
			}
			value = mProperties.getProperty(KEY_PASSENGERS);
			if(value != null){
				setPassengers(value);
			}
			value = mProperties.getProperty(KEY_PRIORITY_TYPE);
			if(value != null){
				setPriorityType(value);
			}
			value = mProperties.getProperty(KEY_TRAIN_TYPE_FILTER);
			if(value != null){
				setTrainTypeFilter(value);
			}
			value = mProperties.getProperty(KEY_AUTO_QUERY);
			if(value != null){
				setQueryAuto(value);
			}
			value = mProperties.getProperty(KEY_QUERY_MODE);
			if(value != null){
				setQueryMode(value);
			}
			value = mProperties.getProperty(KEY_SUBMIT_WITHOUT_ENOUGH_TICKET);
			if(value != null){
				setSubmitWithoutEnoughTicket(value);
			}			
		} catch (IOException e) {
			Log.i("loadUserInfo,e="+e);
		}
	}
	
	public void saveUserInfo(){
		mProperties.setProperty(KEY_USER_NAME, mUserName);
		mProperties.setProperty(KEY_USER_PW, mUserPw);
		if(mFromStationName != null){
			mProperties.setProperty(KEY_FROM_STATION_NAME, mFromStationName);
		}
		if(mToStationName != null){
			mProperties.setProperty(KEY_TO_STATION_NAME, mToStationName);
		}
		if(mFromStationCode != null){
			mProperties.setProperty(KEY_FROM_STATION_CODE, mFromStationCode);
		}
		if(mToStationCode != null){
			mProperties.setProperty(KEY_TO_STATION_CODE, mToStationCode);
		}
		if(mDate != null){
			mProperties.setProperty(KEY_DATE, mDate);
		}
		if(mSeatFilter != null){
			mProperties.setProperty(KEY_SEAT_FILTER, mSeatFilter);
		}else{
			mProperties.remove(KEY_SEAT_FILTER);
		}
		if(mTrainFilter != null){
			mProperties.setProperty(KEY_TRAIN_FILTER, mTrainFilter);
		}else{
			mProperties.remove(KEY_TRAIN_FILTER);
		}
		if(mPassengers != null){
			mProperties.setProperty(KEY_PASSENGERS, mPassengers);
		}
		if(mPriorityType != null){
			mProperties.setProperty(KEY_PRIORITY_TYPE, mPriorityType);
		}
		if(mTrainTypeFilter != null){
			mProperties.setProperty(KEY_TRAIN_TYPE_FILTER, mTrainTypeFilter);
		}
		if(mQueryAuto != null){
			mProperties.setProperty(KEY_AUTO_QUERY, mQueryAuto);
		}
		if(mQueryMode != null){
			mProperties.setProperty(KEY_QUERY_MODE, mQueryMode);
		}
		if(mSubmitWithoutEnough != null){
			mProperties.setProperty(KEY_SUBMIT_WITHOUT_ENOUGH_TICKET, mSubmitWithoutEnough);
		}
		
		try{
			FileOutputStream out = new FileOutputStream(new File(USER_PROPERTY_URL));
			Log.i(mProperties.toString());
			mProperties.store(out, null);
		}catch(FileNotFoundException e){
			Log.i("saveUserInfo,e="+e);
		}catch(IOException e){
			Log.i("saveUserInfo,e="+e);
		}
	}
}
