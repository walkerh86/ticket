package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import util.Log;

public class HttpDispatcher extends Thread{
	private boolean mQuit = false;
	private final BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private SSLSocketFactory mSslSocketFactory;
	private CookieManager mCookieManager;
	
	public HttpDispatcher(BlockingQueue<MyHttpUrlRequest> requestQueue, CookieManager cookieManager){
		mRequestQueue = requestQueue;
		mCookieManager = cookieManager;
		
		try{
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { tm }, null);
			mSslSocketFactory = sslcontext.getSocketFactory();		
			HttpsURLConnection.setDefaultSSLSocketFactory(mSslSocketFactory);
		}catch( KeyManagementException e){
			System.out.print("init http e="+e+"\n");
		}catch(NoSuchAlgorithmException e){
			System.out.print("init http e="+e+"\n");
		}
	}
	
	@Override
	public void run(){
		MyHttpUrlRequest request;
		
		while(true){
			try {
                // Take a request from the queue.
                request = mRequestQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }
			boolean success = false;
			int retry_count = 0;
			while(!success){
				success = processRequest(request);
				if (mQuit) {
                    return;
                }
				if(!success){
					retry_count++;
					if(retry_count >3){
						break;
					}
					try{
						Thread.sleep(500);
					}catch(InterruptedException e){
						
					}
				}
			}
		}
	}
	
	private boolean processRequest(MyHttpUrlRequest request){
		InputStream is = null;
		boolean success = false;
		
		try{
			Map<String,String> params = request.getParams();
			String requestUrl = request.getUrl();
			if(request.getType().equals("GET") && params != null && params.size() > 0){
				requestUrl += "?";
				String content = new String();
				for(Map.Entry<String, String>entry : params.entrySet()){
					if(content.length() > 0){
						content += "&";
					}
					content += getUrlEncodeString(entry.getKey()) + "="
				              + getUrlEncodeString(entry.getValue());
				}
				requestUrl += content;
			}
			Log.i("httpDispatcher requestUrl="+requestUrl);
			URL url = new URL(requestUrl);
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			
			Map<String,String>headers = request.getHeaders();
			if(headers != null && headers.size() > 0){					
				for(Map.Entry<String, String>entry : headers.entrySet()){
					connection.addRequestProperty(entry.getKey(),entry.getValue());
				}
			}			
			String cookie = mCookieManager.getCookieString();
			if(cookie != null){
				Log.i("cookie="+cookie);
				connection.addRequestProperty("Cookie",cookie);
			}
			
			//Map<String,String> params = request.getParams();
			if(request.getType().equals("POST") && params != null && params.size() > 0){
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				String content = new String();
				for(Map.Entry<String, String>entry : params.entrySet()){
					if(content.length() > 0){
						content += "&";
					}
					//content += URLEncoder.encode(entry.getKey(), "UTF-8") + "="
				    //          + URLEncoder.encode(entry.getValue(), "UTF-8");
					content += entry.getKey() + "="
				              + entry.getValue();
				}
				Log.i("httpDispatcher param:\n"+content);
				byte[] bypes = content.getBytes();
				connection.getOutputStream().write(bypes);
			}
			
			is = connection.getInputStream();
			String encoding = connection.getContentEncoding();
			if(encoding != null && encoding.equals("gzip")){
				is = new GZIPInputStream(is);
			}
			MyHttpResponse<?> response = request.getResponse();
			if(response != null){
				response.parseResponse(connection.getResponseCode(),connection.getResponseMessage()
						,is,connection.getHeaderFields());
				response.deliverResponse();
			}
			success = true;
			Log.i("httpDispatcher end\n");
		}catch(MalformedURLException e){
			Log.i("httpDispatcher e="+e);
		}catch(IOException e){
			Log.i("httpDispatcher e="+e);
		}finally{
			try{
				if(is != null){
					is.close();
				}
			}catch(IOException e){
				Log.i("httpDispatcher e="+e);
			}
		}
		
		return success;
	}
	
	private String getUrlEncodeString(String src){
		String dst = null;
		try{
			dst = URLEncoder.encode(src, "UTF-8");
		}catch(UnsupportedEncodingException e){
			Log.i("getUrlEncodeString e="+e);
		}
		
		return dst;
	}
	
	private static X509TrustManager tm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
}
