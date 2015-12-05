import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;

import util.Log;
import util.UrlConstants;
import net.CookieManager;
import net.HttpHeader;
import net.HttpResponseHandler;
import net.ImageHttpResponse;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StringHttpResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;


public class ProcessLogin implements HttpResponseHandler,UiActionListener{
	public static final int STEP_GET_COOKIE = 1;
	public static final int STEP_GET_LOGIN_CAPTCHA = 2;
	public static final int STEP_CHECK_CAPTCHA_CODE = 3;
	public static final int STEP_LOGIN_AYNC_SUGGEST = 4;
	public static final int STEP_LOGIN_REQUEST = 5;
	public static final int STEP_LOGIN_INIT = 6;

	public static final int STEP_LOGIN = 7;
	
	private Object mLock = new Object();
	private UiInterface mCallBack;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	
	private FrameLogin mFrameLogin;
	
	public ProcessLogin(UiInterface cb, BlockingQueue<MyHttpUrlRequest> queue, UserInfo userInfo){			
		mCallBack = cb;
		mRequestQueue = queue;
		mUserInfo = userInfo;
		
		initLoginUi();
		stepGetCookie();
	}
	
	private void initLoginUi(){
		if(mFrameLogin == null){
			mFrameLogin = new FrameLogin(this);
			mFrameLogin.setUserInfo(mUserInfo);
		}
		mFrameLogin.setVisible(true);
	}
	
	public void setUiVisible(boolean visible){
		mFrameLogin.setVisible(visible);
	}
	
	@Override
	public void onUiAction(int action){
		if(action == UiActionListener.UI_ACTION_USER_LOGIN){
			//stepLoginAyncSuggest();
			stepCheckRandCodeAnsyn();
		}else if(action == UiActionListener.UI_ACTION_UPDATE_CAPTCHA){
			stepGetLoginCaptcha();
		}/*else if(action == UiActionListener.UI_ACTION_CHECK_CAPTCHA){
			stepCheckRandCodeAnsyn();
		}*/
	}
	

	public void stepGetCookie(){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_COOKIE_URL,"GET",
				HttpHeader.getHeader(null),null,
				new StringHttpResponse(this,STEP_GET_COOKIE)));
	}

	public void stepLoginInit(){
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/login/init","GET",
				HttpHeader.getHeader("https://kyfw.12306.cn/otn/"),null,
				new StringHttpResponse(this,STEP_LOGIN_INIT)));
	}

	public void stepLogin(){
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/login/","GET",
				HttpHeader.getHeader("https://kyfw.12306.cn/otn/login/init"),null,
				new StringHttpResponse(this,STEP_LOGIN)));
	}
	
	public void stepGetLoginCaptcha(){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_LOGIN_CAPTCHA_URL,"GET",
				HttpHeader.getHeader(UrlConstants.REF_LOGINPASSCODE_URL),null,
				new ImageHttpResponse(UrlConstants.FILE_LOGIN_CAPTCHA_URL,this,STEP_GET_LOGIN_CAPTCHA)));
	}
	
	public void stepCheckRandCodeAnsyn(){		
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("rand", "sjrand");
		params.put("randCode", mFrameLogin.getCaptchaCode());
		Log.i("capta="+mFrameLogin.getCaptchaCode());
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn","POST",
				HttpHeader.getHeader(UrlConstants.REF_LOGINPASSCODE_URL),params,
				new StringHttpResponse(this,STEP_CHECK_CAPTCHA_CODE)));
	}
	
	public void stepLoginAyncSuggest(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("loginUserDTO.user_name", mUserInfo.getUserName());
		params.put("userDTO.password", mUserInfo.getUserPw());
		params.put("randCode", mUserInfo.getCaptchaCode());
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.GET_LOGIN_AYSN_SUGGEST_URL,"POST",
				HttpHeader.getHeader(UrlConstants.REF_LOGINPASSCODE_URL),params,
				new StringHttpResponse(this,STEP_LOGIN_AYNC_SUGGEST)));
	}
	
	public void stepLoginRequest(){
		/*
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att", "");
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/login/userLogin","POST",
				HttpHeader.login(),params,
				new StringHttpResponse(this,STEP_LOGIN_REQUEST)));
		*/
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/index/init","GET",
				HttpHeader.getHeader(UrlConstants.REF_LOGINPASSCODE_URL),null,
				new StringHttpResponse(this,STEP_LOGIN_INIT)));
	}	
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){		
			Log.i("handleResponse,mStep ="+response.mStep);
			if(response.mStep == STEP_GET_COOKIE){
				StringHttpResponse strResponse = (StringHttpResponse)response;
				CookieManager cookieManager = MainApp.getCookieManager();
				cookieManager.setCookie(strResponse.mHeaders);
				Log.i(cookieManager.getCookieString());
				
				//stepGetLoginCaptcha();
				stepLoginInit();
			}else if(response.mStep == STEP_LOGIN_INIT){
				StringHttpResponse strResponse = (StringHttpResponse)response;
				CookieManager cookieManager = MainApp.getCookieManager();
				cookieManager.setCookie(strResponse.mHeaders);
				Log.i(cookieManager.getCookieString());
				
				stepGetLoginCaptcha();
				//stepLogin();
			}else if(response.mStep == STEP_LOGIN){
				stepGetLoginCaptcha();
			}else if(response.mStep == STEP_GET_LOGIN_CAPTCHA){
				ImageHttpResponse imgResponse = (ImageHttpResponse)response;
				//mCallBack.setLoginCaptcha(imgResponse.mResult);
				mFrameLogin.setCaptchaIcon(imgResponse.mResult);
			}else if(response.mStep == STEP_CHECK_CAPTCHA_CODE){
				StringHttpResponse strResponse = (StringHttpResponse)response;
				parseCheckRandCodeAnsyn(strResponse.mResult);				
			}else if(response.mStep == STEP_LOGIN_AYNC_SUGGEST){
				Log.i("code="+response.mResponseMsg);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;					
					//stepLoginRequest();
					parseLoginAysnSuggest(strResponse.mResult);
				}
			}else if(response.mStep == STEP_LOGIN_REQUEST){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//StringHttpResponse strResponse = (StringHttpResponse)response;
					//Log.i(strResponse.mResult);
					//mCallBack.loginSuccess();
				}
			}else if(response.mStep == STEP_LOGIN_INIT){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//StringHttpResponse strResponse = (StringHttpResponse)response;
					//Log.i(strResponse.mResult);
					mCallBack.loginSuccess();
				}
			}
		}
	}
	
	private void parseCheckRandCodeAnsyn(String response){
		Log.i("parseCheckRandCodeAnsyn:"+response);
		try{
			JSONObject jObj = JSONObject.fromObject(response);
			String data = jObj.getString("data");
			JSONObject jObjData = JSONObject.fromObject(data);
			String result = jObjData.getString("result");
			
			Log.i(("parseCheckRandCodeAnsyn:result="+result));
			if(result.equals("1")){
				stepLoginAyncSuggest();
			}else{
				mFrameLogin.checkCaptchaCodeFail();
			}
		}catch(JSONException e){
		}
	}
	
	private void parseLoginAysnSuggest(String response){
		Log.i("parseLoginAysnSuggest:"+response);
		String message = null;
		try{
			JSONObject jObj = JSONObject.fromObject(response);
			String data = jObj.getString("data");
			JSONObject jData = JSONObject.fromObject(data);
			String loginCheck = jData.getString("loginCheck");
			if(loginCheck.equals("Y")){
				//stepLoginRequest();
				mCallBack.loginSuccess();
			}else{
				message = jObj.getString("messages");
			}
		}catch(JSONException e){
			//error
			if(message != null){
				mFrameLogin.showLog(message);
			}
			//must update captcha;
			stepGetLoginCaptcha();
		}
	}	
}
