package net;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHttpResponse<T> {
	public int mResponseCode;
	public String mResponseMsg;
	//private byte[] mContents;
	public Map<String,List<String>>mHeaders;
	public T mResult;
	private HttpResponseHandler mHandler;
	public int mStep;
	
	public MyHttpResponse(HttpResponseHandler handler, int step){
		mHandler = handler;
		mStep = step;
	}
	
	public void parseResponse(int code, String msg, InputStream is, Map<String,List<String>> headers){
		mResponseCode = code;
		mResponseMsg = new String(msg);
		mHeaders = headers;//new HashMap<String,List<String>>(headers.size());
		mResult = parseContent(is);
	}
	
	public void deliverResponse(){
		mHandler.handleResponse(this);
	}
	
	protected T parseContent(InputStream is){
		return null;
	}
}
