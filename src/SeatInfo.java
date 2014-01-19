import java.util.ArrayList;
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

	private static final HashMap<String,SeatTypeInfo> mAllSeatTypeMap = new HashMap<String,SeatTypeInfo>(SEAT_TYPE_NUM);
	static{
		mAllSeatTypeMap.put("sw",new SeatTypeInfo("swz_num","9","商务座"));
		mAllSeatTypeMap.put("tz",new SeatTypeInfo("tz_num","P","特等座"));
		mAllSeatTypeMap.put("zy",new SeatTypeInfo("zy_num","M","一等座"));
		mAllSeatTypeMap.put("ze",new SeatTypeInfo("ze_num","O","二等座"));
		mAllSeatTypeMap.put("gr",new SeatTypeInfo("gr_num","5","高级软卧"));
		mAllSeatTypeMap.put("rw",new SeatTypeInfo("rw_num","4","软卧"));
		mAllSeatTypeMap.put("yw",new SeatTypeInfo("yw_num","3","硬卧"));
		mAllSeatTypeMap.put("rz",new SeatTypeInfo("rz_num","2","软座"));
		mAllSeatTypeMap.put("yz",new SeatTypeInfo("yz_num","1","硬座"));
		mAllSeatTypeMap.put("wz",new SeatTypeInfo("wz_num","1","无座"));
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
			checkBoxs[index].setName(seatType.mCode);
			index++;
		}
		return checkBoxs;
	}
	
	public static JCheckBox[] getAllSeatTypeCheckBoxs(){		
		JCheckBox[] checkBoxs = new JCheckBox[mAllSeatTypeMap.size()];
		Iterator<Entry<String, SeatTypeInfo>> iter = mAllSeatTypeMap.entrySet().iterator();
		int index = 0;
		while (iter.hasNext()) {
			Map.Entry<String,SeatTypeInfo> entry = (Map.Entry<String,SeatTypeInfo>)iter.next();
			SeatTypeInfo seatType = (SeatTypeInfo)entry.getValue();
			checkBoxs[index] = new JCheckBox();
			checkBoxs[index].setText(seatType.mName);
			checkBoxs[index].setName(entry.getKey());
			index++;
		}
		return checkBoxs;
	}
}
