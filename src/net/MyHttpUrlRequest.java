package net;

import java.util.List;
import java.util.Map;

public class MyHttpUrlRequest{
	private String mUrl;
	private String mType="GET"; //get/post
	private Map<String,String> mHeaders;
	private Map<String,String> mParams;
	private MyHttpResponse<?> mResponse;
	
	public MyHttpUrlRequest(String url,String type, Map<String,String>headers,
			Map<String,String>params, MyHttpResponse<?> response){
		mUrl = url;
		if(type != null){
			mType = type;
		}
		mHeaders = headers;
		mParams = params;
		mResponse = response;
	}
	
	public String getUrl(){
		return mUrl;
	}
	
	public String getType(){
		return mType;
	}
	
	public Map<String,String> getHeaders(){
		return mHeaders;
	}
	
	public Map<String,String> getParams(){
		return mParams;
	}
	
	public MyHttpResponse<?> getResponse(){
		return mResponse;
	}
}
