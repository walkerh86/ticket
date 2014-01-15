import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;

import util.Log;
import util.UrlConstants;

import net.HttpHeader;
import net.HttpResponseHandler;
import net.ImageHttpResponse;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StatusHttpResponse;
import net.StringHttpResponse;
import net.CookieManager;


public class RequestProcess implements HttpResponseHandler{
	private static final int STEP_GET_COOKIE = 1;
	private static final int STEP_GET_LOGIN_CAPTCHA = 2;
	private static final int STEP_LOGIN_AYNC_SUGGEST = 3;
	private static final int STEP_LOGIN_REQUEST = 4;
	private static final int STEP_QUERY_LEFT = 5;
	
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private Object mLock = new Object();
	private UiInterface mCallBack;
	private UserInfo mUserInfo;
	
	public RequestProcess(BlockingQueue<MyHttpUrlRequest> queue, UiInterface cb, UserInfo userInfo){
		mRequestQueue = queue;
		mCallBack = cb;
		mUserInfo = userInfo;
	}
	
	public void stepGetCookie(){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_COOKIE_URL,"GET",
				HttpHeader.loginInitHearder(),null,new StringHttpResponse(this,STEP_GET_COOKIE)));
	}
	
	public void stepGetLoginCaptcha(){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_LOGIN_CAPTCHA_URL,"GET",
				HttpHeader.getPassCode(true),null,
				new ImageHttpResponse(UrlConstants.FILE_LOGIN_CAPTCHA_URL,this,STEP_GET_LOGIN_CAPTCHA)));
	}
	
	public void stepLoginAyncSuggest(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("loginUserDTO.user_name", mUserInfo.getUserName());
		params.put("userDTO.password", mUserInfo.getUserPw());
		params.put("randCode", mUserInfo.getCaptchaCode());
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_LOGIN_AYSN_SUGGEST_URL,"POST",
				HttpHeader.postCheckCode(),params,
				new ImageHttpResponse(UrlConstants.FILE_LOGIN_CAPTCHA_URL,this,STEP_LOGIN_AYNC_SUGGEST)));
	}
	
	public void stepLoginRequest(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att", "");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_LOGIN_URL,"POST",
				HttpHeader.login(),params,
				new StatusHttpResponse(this,STEP_LOGIN_REQUEST)));
	}
	
	public void stepQueryLeft(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("leftTicketDTO.train_date", mUserInfo.getDate());
		params.put("leftTicketDTO.from_station", mUserInfo.getFromStationCode());
		params.put("leftTicketDTO.to_station", mUserInfo.getToStationCode());
		params.put("purpose_codes", "ADULT");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_TIKETSEARCH_URL,"GET",
				HttpHeader.tiketSearch(),params,
				new StringHttpResponse(this,STEP_QUERY_LEFT)));
	}
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){			
			if(response.mStep == STEP_GET_COOKIE){
				Log.i("handleResponse,mStep == STEP_GET_COOKIE");
				StringHttpResponse strResponse = (StringHttpResponse)response;
				CookieManager cookieManager = MainApp.getCookieManager();
				cookieManager.setCookie(strResponse.mHeaders);
				Log.i(cookieManager.getCookieString());
				
				stepGetLoginCaptcha();
			}else if(response.mStep == STEP_GET_LOGIN_CAPTCHA){
				Log.i("handleResponse,mStep == STEP_GET_LOGIN_CAPTCHA");
				ImageHttpResponse imgResponse = (ImageHttpResponse)response;
				mCallBack.setLoginCaptcha(imgResponse.mResult);
			}else if(response.mStep == STEP_LOGIN_AYNC_SUGGEST){
				Log.i("handleResponse,mStep == STEP_LOGIN_AYNC_SUGGEST");
				Log.i("code="+response.mResponseMsg);
				if(response.mResponseCode == 200){
					stepLoginRequest();
				}
			}else if(response.mStep == STEP_LOGIN_REQUEST){
				Log.i("handleResponse,mStep == STEP_GET_LOGIN_REQUEST");
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//stepLoginRequest();
					mCallBack.loginSuccess();
					//stepQueryLeft();
				}
			}else if(response.mStep == STEP_QUERY_LEFT){
				Log.i("handleResponse,mStep == STEP_QUERY_LEFT");
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					//Log.i(strResponse.mResult);
					mCallBack.parseTicketQuery(strResponse.mResult);
				}
			}
		}
	}
}
