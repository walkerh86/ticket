package net;

public interface HttpResponseHandler {
	public void handleResponse(MyHttpResponse<?> response);
}
