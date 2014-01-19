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
	private static final String KEY_PRIORITY_TYPE="priority_type";
	
	public static final String PRIORITY_TYPE_NONE = "none";
	public static final String PRIORITY_TYPE_SEAT = "seat";
	public static final String PRIORITY_TYPE_TRAIN = "train";
	
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
	private String mPriorityType = PRIORITY_TYPE_NONE;
	private String[] mSeatFilter;
	private String[] mTrainFilter;	
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
	
	public String[] getSeatFitler(){
		return mSeatFilter;
	}
		
	public void setSeatFilter(String[] filter){
		mSeatFilter = filter;
	}
	
	public void setSeatFilter(String filter){
		if(filter == null || filter.length() == 0){
			return;
		}
		mSeatFilter = filter.split("[,]");
		Log.i("setSeatFilter,len="+mSeatFilter.length+",filter="+filter);
	}
	
	public String[] getTrainFitler(){
		return mTrainFilter;
	}
		
	public void setTrainFilter(String[] filter){
		mTrainFilter = filter;
	}
	
	public void setTrainFilter(String filter){
		if(filter == null || filter.length() == 0){
			return;
		}
		mTrainFilter = filter.split("[,]");
	}
	
	public String getPriorityType(){
		return mPriorityType;
	}
	
	public void setPriorityType(String type){
		mPriorityType = type;
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
			mProperties.setProperty(KEY_SEAT_FILTER, getString(mSeatFilter));
		}
		if(mTrainFilter != null){
			mProperties.setProperty(KEY_TRAIN_FILTER, getString(mTrainFilter));
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
	
	private static String getString(String[] strs){
		if(strs.length == 0){
			return null;
		}
		
		String result = "";
		for(int i=0;i<strs.length;i++){
			if(result.length() > 0){
				result += ",";
			}
			result += strs[i];
		}
		
		return result;
	}
}
