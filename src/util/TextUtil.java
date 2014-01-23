package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TextUtil {
	public static boolean isEmpty(String text){
		return (text == null) || text.length() == 0;
	}
	
	public static String getString(String[] strs){
		if(strs == null || strs.length == 0){
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
	
	public static String getUrlEncodeString(String src){
		String dst = null;
		try{
			dst = URLEncoder.encode(src, "UTF-8");
		}catch(UnsupportedEncodingException e){
			Log.i("getUrlEncodeString e="+e);
		}
		
		return dst;
	}
}
