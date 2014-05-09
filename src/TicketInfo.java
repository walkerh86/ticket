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
	public int mSeatTicketNum;
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
		mSeatCodeMap.put("qt_num","其他");
	}
	*/
	public static TicketInfo getTicketInfoFromJSONObject(JSONObject jObj,String seatType){
		TicketInfo ticketInfo = new TicketInfo();
		ticketInfo.mSecretStr = jObj.getString(TicketInfoConstants.KEY_SECRET_STR);
		
		JSONObject queryLeftNewDTO = jObj.getJSONObject("queryLeftNewDTO");
		ticketInfo.mStationTrainCode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
		ticketInfo.mFromStationTelecode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_FROM_STATION_TELECODE);
		ticketInfo.mToStationTelecode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_TO_STATION_TELECODE);
		ticketInfo.mTrainNo = queryLeftNewDTO.getString(TicketInfoConstants.KEY_TRAIN_NO);
		ticketInfo.mFromStationName = queryLeftNewDTO.getString(TicketInfoConstants.KEY_FROM_STATION_NAME);
		ticketInfo.mToStationName = queryLeftNewDTO.getString(TicketInfoConstants.KEY_TO_STATION_NAME);
		ticketInfo.mLocationCode = queryLeftNewDTO.getString(TicketInfoConstants.KEY_LOCATION_CODE);
		ticketInfo.mYpInfo = queryLeftNewDTO.getString(TicketInfoConstants.KEY_YP_INFO);
		
		ticketInfo.mSeatTypeName = SeatInfo.getSeatName(seatType);
		ticketInfo.mSeatTypeCode = SeatInfo.getSeatCode(seatType);
		String seatTicketNumStr = queryLeftNewDTO.getString(SeatInfo.getSeatNumKey(seatType));
		ticketInfo.mSeatTicketNum = Integer.valueOf(SeatInfo.checkSeatNum(seatTicketNumStr));
				
		return ticketInfo;
	}
	
	public static String getStartEndTime(JSONObject train){		
		return train.getString(TicketInfoConstants.KEY_START_TIME)+" - "+train.getString(TicketInfoConstants.KEY_ARRIVE_TIME);
	}
	
	public static String getStartEndStation(JSONObject train){
		String startStationCode = train.getString(TicketInfoConstants.KEY_START_STATION_TELECODE);
		String fromStationCode = train.getString(TicketInfoConstants.KEY_FROM_STATION_TELECODE);
		String startString = "(过)";
		if(startStationCode.equals(fromStationCode)){
			startString = "(始)";
		}
		return startString+train.getString(TicketInfoConstants.KEY_FROM_STATION_NAME)+" - (终)"+train.getString(TicketInfoConstants.KEY_TO_STATION_NAME);
	}
	
	public String toString(){
		return mStationTrainCode+","+mFromStationName+","+mToStationName+","+mSeatTypeName;
	}
}
