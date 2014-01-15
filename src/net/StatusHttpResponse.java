package net;

import java.io.InputStream;

public class StatusHttpResponse extends MyHttpResponse<String>{
	public StatusHttpResponse(HttpResponseHandler handler,int step){
		super(handler,step);
	}
	
	@Override
	protected String parseContent(InputStream is){
		return null;
	}
}
