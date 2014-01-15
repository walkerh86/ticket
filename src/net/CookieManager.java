package net;
import java.util.List;
import java.util.Map;

import util.Log;


public class CookieManager {
	private String mJSessionId;
	private String mBigIpServerOtn;
	private String mExtraCookie;
	
	public CookieManager(){
		
	}
	
	public void setCookie(Map<String,List<String>>headers){
		for(Map.Entry<String, List<String>> entry : headers.entrySet()){
			if(entry.getKey() != null && entry.getKey().equals("Set-Cookie")){
				//String cookie = entry.getValue().toString();
				for(String cookie : entry.getValue()){
					String cookieName = cookie.split("=")[0];
					String cookieValue = cookie.split("=")[1].split(";")[0];
					if (cookieName.equals("JSESSIONID")) {
						mJSessionId = cookieValue;
					}
					if (cookieName.equals("BIGipServerotn")) {
						mBigIpServerOtn = cookieValue;
					}
				}
			}
		}
	}
	
	public void add(Map<String,String> cookieMap){
		mExtraCookie = new String();
		for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
			mExtraCookie += "; " + entry.getKey() + "=" + entry.getValue();
		}
	}
	
	public String getCookieString(){
		if(mJSessionId == null || mBigIpServerOtn == null){
			return null;
		}
		return "JSESSIONID=" + mJSessionId + mExtraCookie + ";BIGipServerotn=" + mBigIpServerOtn;
	}
}
