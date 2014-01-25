import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;

import util.Log;


public class SeatInfo {	
	public static final int SEAT_TYPE_NUM = 12;
	
	public static final String KEY_SW = "sw";
	public static final String KEY_TZ = "tz";
	public static final String KEY_ZY = "zy";
	public static final String KEY_ZE = "ze";
	public static final String KEY_GR = "gr";
	public static final String KEY_RW = "rw";
	public static final String KEY_YW = "yw";
	public static final String KEY_RZ = "rz";
	public static final String KEY_YZ = "yz";
	public static final String KEY_WZ = "wz";

	private static final HashMap<String,SeatTypeInfo> mAllSeatTypeMap = new LinkedHashMap<String,SeatTypeInfo>(SEAT_TYPE_NUM);
	static{
		mAllSeatTypeMap.put(KEY_SW,new SeatTypeInfo("swz_num","9","商务座"));
		mAllSeatTypeMap.put(KEY_TZ,new SeatTypeInfo("tz_num","P","特等座"));
		mAllSeatTypeMap.put(KEY_ZY,new SeatTypeInfo("zy_num","M","一等座"));
		mAllSeatTypeMap.put(KEY_ZE,new SeatTypeInfo("ze_num","O","二等座"));
		mAllSeatTypeMap.put(KEY_GR,new SeatTypeInfo("gr_num","5","高级软卧"));
		mAllSeatTypeMap.put(KEY_RW,new SeatTypeInfo("rw_num","4","软卧"));
		mAllSeatTypeMap.put(KEY_YW,new SeatTypeInfo("yw_num","3","硬卧"));
		mAllSeatTypeMap.put(KEY_RZ,new SeatTypeInfo("rz_num","2","软座"));
		mAllSeatTypeMap.put(KEY_YZ,new SeatTypeInfo("yz_num","1","硬座"));
		mAllSeatTypeMap.put(KEY_WZ,new SeatTypeInfo("wz_num","1","无座"));
	}
	
	private LinkedHashSet<String> mSelectedSeatType;
	
	public SeatInfo(){
		mSelectedSeatType = new LinkedHashSet<String>(SEAT_TYPE_NUM);
	}
	
	public void selectSingleSeatType(String key){
		Log.i("selectSingleSeatType,key="+key);
		if(mSelectedSeatType.contains(key)){
			return;
		}
		Log.i("selectSingleSeatType,add"+key);
		mSelectedSeatType.add(key);
	}
	
	public void unSelectSingleSeatType(String key){
		if(mSelectedSeatType.contains(key)){
			mSelectedSeatType.remove(key);
		}
	}
	
	public void selectMultiSeatType(String[] seats){
		if(seats == null || seats.length == 0){
			return;
		}
		for(int i=0;i<seats.length;i++){
			Log.i("selectMultiSeatType,seat="+seats[i]);
			selectSingleSeatType(seats[i]);
		}
	}
	
	public String[] getSelectedSeatTypes(){
		int count = mSelectedSeatType.size();
		String[] seatTypes = new String[count];
		int i=0;
		for(String seatType : mSelectedSeatType){
			seatTypes[i] = seatType;
			i++;
		}
		return seatTypes;
	}
	
	public static String getSeatName(String key){
		if(mAllSeatTypeMap.containsKey(key)){
			return mAllSeatTypeMap.get(key).mName;
		}
		return null;
	}
	
	public static String getSeatCode(String key){
		if(mAllSeatTypeMap.containsKey(key)){
			return mAllSeatTypeMap.get(key).mCode;
		}
		return null;
	}
	
	public static String getSeatNumKey(String key){
		if(mAllSeatTypeMap.containsKey(key)){
			return mAllSeatTypeMap.get(key).mNumKey;
		}
		return null;
	}
	
	public JCheckBox[] getSelectedSeatTypeCheckBoxs(){		
		int count = mSelectedSeatType.size();
		if(count == 0){
			return null;
		}
		
		JCheckBox[] checkBoxs = new JCheckBox[count];
		int index = 0;
		for(String key : mSelectedSeatType){
			Log.i("getSelectedSeatTypeCheckBoxs,key="+key);
			SeatTypeInfo seatType = mAllSeatTypeMap.get(key);
			checkBoxs[index] = new JCheckBox();
			checkBoxs[index].setText(seatType.mName);
			checkBoxs[index].setName(key);
			index++;
		}
		return checkBoxs;
	}
	
	public static HashMap<String,SeatTypeInfo> getAllSeatTypeInfo(){
		return mAllSeatTypeMap;
	}
	
	private static final int ENOUGH_SEAT_NUM = 100;
	public static int checkSeatNum(String seatNum){
		int num = 0;
		if(seatNum.equals("有")){
			num = ENOUGH_SEAT_NUM;
		}else if(seatNum.equals("无") || seatNum.equals("--") || seatNum.equals("*")){
			num = 0;
		}else{
			num = Integer.valueOf(seatNum);
		}
		return num;
	}
}
