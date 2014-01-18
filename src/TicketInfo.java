import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import util.Log;
import util.TicketInfoConstants;
import net.sf.json.JSONObject;


public class TicketInfo {
	//from JSONObject
	public String mStationTrainCode;
	public String mTrainNo;
	public String mFromStationName;
	public String mToStationName;
	public String mFromStationTelecode;
	public String mToStationTelecode;
	public String mLocationCode;
	public String mYpInfo;		
	//from JSONObject
	public String mSecretStr;
	//from convert
	public String mSeatTypeCode;
	public String mSeatTypeName;
	/*
	public static final HashMap<String,String> mSeatCodeMap = new HashMap<String,String>();
	static{
		mSeatCodeMap.put("swz_num","9");
		mSeatCodeMap.put("tz_num","P");
		mSeatCodeMap.put("zy_num","M");
		mSeatCodeMap.put("ze_num","O");
		mSeatCodeMap.put("gr_num","5");
		mSeatCodeMap.put("rw_num","4");
		mSeatCodeMap.put("yw_num","3");
		mSeatCodeMap.put("rz_num","2");	
		mSeatCodeMap.put("yz_num","1");
		mSeatCodeMap.put("wz_num","1");
		mSeatCodeMap.put("qt_num","ÆäËû");
	}
	*/
	public static TicketInfo getTicketInfoFromJSONObject(JSONObject jObj){
		TicketInfo ticketInfo = new TicketInfo();
		ticketInfo.mSecretStr = jObj.getString(TicketInfoConstants.KEY_SECRET_STR);
		//Log.i("getTicketInfoFromJSONObject,mSecretStr=");
		//Log.i(ticketInfo.mSecretStr);
		try{
		Log.i(URLEncoder.encode(ticketInfo.mSecretStr, "UTF-8"));
		}catch( UnsupportedEncodingException e){
			Log.i("getTicketInfoFromJSONObject,e="+e);
		}
		JSONObject queryLeftNewDTO = jObj.getJSONObject("queryLeftNewDTO");
		ticketInfo.mStationTrainCode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
		ticketInfo.mFromStationTelecode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_FROM_STATION_TELECODE);
		ticketInfo.mToStationTelecode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_TO_STATION_TELECODE);
		ticketInfo.mTrainNo = queryLeftNewDTO.getString(TicketInfoConstants.KEY_TRAIN_NO);
		ticketInfo.mFromStationName = queryLeftNewDTO.getString(TicketInfoConstants.KEY_FROM_STATION_NAME);
		ticketInfo.mToStationName = queryLeftNewDTO.getString(TicketInfoConstants.KEY_TO_STATION_NAME);
		ticketInfo.mLocationCode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_LOCATION_CODE);
		ticketInfo.mYpInfo = queryLeftNewDTO.getString(TicketInfoConstants.KEY_YP_INFO);
		return ticketInfo;
	}
	
	public void setSeatType(String type){
		mSeatTypeName = SeatInfo.getSeatName(type);
		mSeatTypeCode = SeatInfo.getSeatCode(type);//mSeatCodeMap.get(type);
		Log.i("setSeatType,type="+type+",mSeatTypeCode="+mSeatTypeCode+",name="+mSeatTypeName);
	}
	
	public String toString(){
		return mStationTrainCode+","+mTrainNo+","+mFromStationName+","+mToStationName+","+mLocationCode
				+","+mFromStationTelecode+","+mToStationTelecode+"\n"
				+mYpInfo+"\n"+mSecretStr;
	}
}
