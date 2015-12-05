package net;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import util.Log;


public class CookieManager {
	private static String KEY_JESSIONID = "JSESSIONID";
	private static String KEY_NRF = "__NRF";
	private static String KEY_BIGIP = "BIGipServerotn";
	private static String KEY_FR_DATE = "_jc_save_fromDate";
	private static String KEY_TO_DATE = "_jc_save_toDate";
	private static String KEY_FR_STATION = "_jc_save_fromStation";
	private static String KEY_TO_STATION = "_jc_save_toStation";
	private static String KEY_WFDC_FLAG = "_jc_save_wfdc_flag";
	private static Map<String, String> mCookieMap = new LinkedHashMap<String, String>();
	static{
		mCookieMap.put(KEY_JESSIONID,"");
		mCookieMap.put(KEY_NRF,"");
		mCookieMap.put(KEY_FR_STATION,"%u6DF1%u5733%2CSZQ");
		mCookieMap.put(KEY_TO_STATION,"%u4FE1%u9633%2CXUN");
		mCookieMap.put(KEY_FR_DATE,"2016-02-01");
		mCookieMap.put(KEY_TO_DATE,"2015-12-04");
		mCookieMap.put(KEY_WFDC_FLAG,"dc");
		mCookieMap.put(KEY_BIGIP,"");
	}
	
	public CookieManager(){
		
	}
	
	public void setCookie(Map<String,List<String>>headers){
		for(Map.Entry<String, List<String>> entry : headers.entrySet()){
			if(entry.getKey() != null && entry.getKey().equals("Set-Cookie")){
				//String cookie = entry.getValue().toString();
				for(String cookie : entry.getValue()){
					String cookieName = cookie.split("=")[0];
					String cookieValue = cookie.split("=")[1].split(";")[0];
					if (cookieName.equals(KEY_JESSIONID)) {
						//mJSessionId = cookieValue;
						mCookieMap.put(KEY_JESSIONID,cookieValue);
					}
					if (cookieName.equals(KEY_BIGIP)) {
						//mBigIpServerOtn = cookieValue;
						mCookieMap.put(KEY_BIGIP,cookieValue);
					}
					if (cookieName.equals(KEY_NRF)) {
						//mBigIpServerOtn = cookieValue;
						mCookieMap.put(KEY_NRF,cookieValue);
					}
				}
			}
		}
	}
	
	public void add(Map<String,String> cookieMap){
		for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
			mCookieMap.put(entry.getKey(),entry.getValue());
		}
	}
	
	public String getCookieString(){
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		for(Map.Entry<String, String> entry : mCookieMap.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			if(value != null && value.length() > 0){
				if(idx > 0){
					sb.append(";");
				}
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
			idx++;
		}
		return sb.toString();
	}
}
