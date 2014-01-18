import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;

import util.Log;


public class SeatInfo {	
	public static final HashMap<String,String> mSeatMap = new LinkedHashMap<String,String>();	
	static{
		mSeatMap.put("sw","swz_num|9|������");
		mSeatMap.put("tz","tz_num|P|�ص���");
		mSeatMap.put("zy","zy_num|M|һ����");
		mSeatMap.put("ze","ze_num|O|������");
		mSeatMap.put("gr","gr_num|5|�߼�����");
		mSeatMap.put("rw","rw_num|4|����");
		mSeatMap.put("yw","yw_num|3|Ӳ��");
		mSeatMap.put("rz","rz_num|2|����");	
		mSeatMap.put("yz","yz_num|1|Ӳ��");
		mSeatMap.put("wz","wz_num|1|����");
		//mSeatCodeMap.put("0","qt_num|0|����");
	}
	
	public static String getSeatName(String key){
		if(mSeatMap.containsKey(key)){
			String[] val = mSeatMap.get(key).split("[|]");
			return val[2];
		}
		return null;
	}
	
	public static String getSeatCode(String key){
		if(mSeatMap.containsKey(key)){
			Log.i("getSeatCode,value="+mSeatMap.get(key));
			String[] val = mSeatMap.get(key).split("[|]");
			return val[1];
		}
		return null;
	}
	
	public static String getSeatNumKey(String key){
		if(mSeatMap.containsKey(key)){
			String[] val = mSeatMap.get(key).split("[|]");
			return val[0];
		}
		return null;
	}
	
	public static JCheckBox[] getSeatCheckBoxs(){		
		JCheckBox[] checkBoxs = new JCheckBox[mSeatMap.size()];
		Iterator<Entry<String, String>> iter = mSeatMap.entrySet().iterator();
		int index = 0;
		while (iter.hasNext()) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>)iter.next();
			String val = (String)entry.getValue();
			String[] seat = val.split("[|]");		
			checkBoxs[index] = new JCheckBox();
			checkBoxs[index].setText(seat[2]);
			checkBoxs[index].setName(entry.getKey());
			index++;
		}
		return checkBoxs;
	}
}
