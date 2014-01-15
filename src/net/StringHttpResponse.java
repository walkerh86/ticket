package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import util.Log;

public class StringHttpResponse extends MyHttpResponse<String>{
	public StringHttpResponse(HttpResponseHandler handler,int step){
		super(handler,step);
	}
	
	@Override
	protected String parseContent(InputStream is){
		String result = null;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result = sb.toString();
		}catch(IOException e){
			Log.i("StringHttpResponse,e="+e);
		}
		
		return result;
	}
}
