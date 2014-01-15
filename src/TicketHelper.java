import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.StationConstant;


public class TicketHelper {
	private static Map<String, String> cityName2Code = new HashMap<String, String>();

	static {
		String city1[] = StationConstant.stationString1.split("@");
		String city2[] = StationConstant.stationString2.split("@");
		for (String tmp : city1) {
			if (tmp.isEmpty())
				continue;
			String temp[] = tmp.split("\\|");
			cityName2Code.put(temp[1], temp[2]);
		}
		for (String tmp : city2) {
			if (tmp.isEmpty())
				continue;
			String temp[] = tmp.split("\\|");
			cityName2Code.put(temp[1], temp[2]);
		}
	}
	public final static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PART_FORMAT = "yyyy-MM-dd";
	public static final String TIME_PART_FORMAT = "HH:mm:ss.SSS";

	public static String getCityCode(String cityName) {
		return cityName2Code.get(cityName);
	}

	public static String convertStation(String stationName) {
		int length = stationName.length();
		String station = stationName.substring(0, length - 1);
		String stationcode = getCityCode(station);
		if (stationcode != null) {
			return station;
		}
		return stationName;
	}
	
	public static String getUnicode(String cityName, String cityCode) {
		String ret = "";
		String result = "";
		for (int i = 0; i < cityName.length(); i++) {
			int chr1 = (char) cityName.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result += "%u" + Integer.toHexString(chr1).toUpperCase();
			} else {
				result += cityName.charAt(i);
			}
		}
		ret = result + "%2C"+cityCode;
		return ret;
	}
/*
	public static String getOldPassengerStr(List<UserInfo> userInfo) {
		String oldStrs = "";
		for (int i = 0; i < userInfo.size(); i++) {
			String oldStr = userInfo.get(i).getName() + "," + userInfo.get(i).getCardType() + "," + userInfo.get(i).getCardID() + "," + userInfo.get(i).getType();
			oldStrs += oldStr + "_";
		}
		return oldStrs;
	}

	public static String getPassengerTicketStr(List<UserInfo> userInfo) {
		String oldStrs = "";
		for (int i = 0; i < userInfo.size(); i++) {
			String oldStr = "";
			if ("WZ" == userInfo.get(i).getSeatType()) {
			} else {
				oldStr = userInfo.get(i).getSeatType();
			}
			String bR = oldStr + ",0," + userInfo.get(i).getTickType() + "," + userInfo.get(i).getName() + "," + userInfo.get(i).getCardType() + "," + userInfo.get(i).getCardID() + ","
					+ (userInfo.get(i).getPhone() == null ? "" : userInfo.get(i).getPhone()) + ",N";
			oldStrs += bR + "_";
		}
		return oldStrs.substring(0, oldStrs.length() - 1);
	}
	*/
}
