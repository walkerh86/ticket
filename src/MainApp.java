import java.awt.Frame;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.CookieManager;
import net.HttpDispatcher;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;

public class MainApp{
	private static FrameLogin mLoginFrame;
	private static FrameMain mMainFrame;
	private static ArrayBlockingQueue<MyHttpUrlRequest> mRequestQueue;
	//private static ArrayBlockingQueue<MyHttpResponse> mResponseQueue;
	private static HttpDispatcher mHttpDispatcher;
	private static MainProcess mMainProcess;
	private static CookieManager mCookieManager;
	
	public static void main(String args[ ]){
		//mResponseQueue = new ArrayBlockingQueue<MyHttpResponse>(10);
		mRequestQueue = new ArrayBlockingQueue<MyHttpUrlRequest>(10);	
		mCookieManager = new CookieManager();
		mHttpDispatcher = new HttpDispatcher(mRequestQueue,mCookieManager);
		mHttpDispatcher.start();
		PassengerManager passengerManager = PassengerManager.getInstance(); 
		passengerManager.setRequestQueue(mRequestQueue);
				
		mMainProcess = new MainProcess();
		mMainProcess.init(mRequestQueue);
	}
	
	public static BlockingQueue<MyHttpUrlRequest> getRequestQueue(){
		return mRequestQueue;
	}
	
	public static CookieManager getCookieManager(){
		return mCookieManager;
	}
	/*
	public static BlockingQueue<MyHttpResponse> getResponseQueue(){
		return mResponseQueue;
	}*/
}
