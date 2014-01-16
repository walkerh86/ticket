import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;

import util.Log;
import util.TicketInfoConstants;
import util.UrlConstants;

import net.HttpHeader;
import net.HttpResponseHandler;
import net.ImageHttpResponse;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StatusHttpResponse;
import net.StringHttpResponse;
import net.CookieManager;
import net.sf.json.JSONObject;


public class RequestProcess implements HttpResponseHandler{
	public static final int STEP_GET_COOKIE = 1;
	public static final int STEP_GET_LOGIN_CAPTCHA = 2;
	public static final int STEP_LOGIN_AYNC_SUGGEST = 3;
	public static final int STEP_LOGIN_REQUEST = 4;
	public static final int STEP_QUERY_LEFT = 5;
	
	public static final int STEP_SUBMIT_ORDER_CHECK_USER = 10;
	public static final int STEP_SUBMIT_ORDER_REQUEST = 11;
	public static final int STEP_SUBMIT_ORDER_INITDC = 12;
	public static final int STEP_SUBMIT_ORDER_GET_CAPTCHA = 13;
	
	public static final int STEP_INIT_PASSENGERS = 20;
	public static final int STEP_QUERY_PASSENGERS = 21;
	
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
				HttpHeader.loginInitHearder(),null,
				new StringHttpResponse(this,STEP_GET_COOKIE)));
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
	
	//submit order begin
	private JSONObject mSubmitTrain = null;
	private String mTokenSt = null;
	private String mKeyCheckIsChange = null;
	public void startSubmitOrderSequence(JSONObject train){
		mSubmitTrain = train;
		stepSubmitOrderCheckUser();
	}
	
	public void stepSubmitOrderCheckUser(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att","");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_CHECK_USER_URL,"POST",
				HttpHeader.submitOrder(),params,
				new StringHttpResponse(this,STEP_SUBMIT_ORDER_CHECK_USER)));
	}
	public void stepSubmitOrderRequest(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("secretStr",mSubmitTrain.getString(TicketInfoConstants.KEY_SECRET_STR));
		params.put("train_date",mUserInfo.getDate());
		params.put("back_train_dat","");
		params.put("tour_flag","dc");
		params.put("purpose_codes","ADULT");
		params.put("query_from_station_name",mSubmitTrain.getString(TicketInfoConstants.KEY_FROM_STATION_NAME));
		params.put("query_to_station_name",mSubmitTrain.getString(TicketInfoConstants.KEY_TO_STATION_NAME));
		params.put("undefined","");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_SUBMITORDER_URL,"POST",
				HttpHeader.submitOrder(),params,
				new StringHttpResponse(this,STEP_SUBMIT_ORDER_REQUEST)));
	}
	public void stepSubmitOrderInitDc(){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att","");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_INITDC_URL,"POST",
				HttpHeader.initDc(),params,
				new StringHttpResponse(this,STEP_SUBMIT_ORDER_INITDC)));
	}
	public void stepSubmitOrderGetCaptCha(){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_GETSUBPASSCODE_URL,"GET",
				HttpHeader.getPassCode(false),null,
				new ImageHttpResponse(UrlConstants.FILE_SUBMIT_CAPTCHA_URL,this,STEP_SUBMIT_ORDER_GET_CAPTCHA)));
	}
	//submit order end
	
	//get passengers begin
	public void initPassengersRequest(HttpResponseHandler handler){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_PASSENGERS_INIT_URL,"GET",
				HttpHeader.initPassengers(),null,
				new StringHttpResponse(handler,STEP_INIT_PASSENGERS)));
	}
	
	public void queryPassengersRequest(HttpResponseHandler handler, int pageIdex, int pageSize){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("pageIndex",Integer.toString(pageIdex));
		params.put("pageSize",Integer.toString(pageSize));
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_PASSENGERS_QUERY_URL,"POST",
				HttpHeader.initPassengers(),params,
				new StringHttpResponse(handler,STEP_QUERY_PASSENGERS)));
	}
	//get passengers end
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){		
			Log.i("handleResponse,mStep ="+response.mStep);
			if(response.mStep == STEP_GET_COOKIE){
				StringHttpResponse strResponse = (StringHttpResponse)response;
				CookieManager cookieManager = MainApp.getCookieManager();
				cookieManager.setCookie(strResponse.mHeaders);
				Log.i(cookieManager.getCookieString());
				
				stepGetLoginCaptcha();
			}else if(response.mStep == STEP_GET_LOGIN_CAPTCHA){
				ImageHttpResponse imgResponse = (ImageHttpResponse)response;
				mCallBack.setLoginCaptcha(imgResponse.mResult);
			}else if(response.mStep == STEP_LOGIN_AYNC_SUGGEST){
				Log.i("code="+response.mResponseMsg);
				if(response.mResponseCode == 200){
					stepLoginRequest();
				}
			}else if(response.mStep == STEP_LOGIN_REQUEST){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//stepLoginRequest();
					mCallBack.loginSuccess();
					//stepQueryLeft();
				}
			}else if(response.mStep == STEP_QUERY_LEFT){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					//Log.i(strResponse.mResult);
					mCallBack.parseTicketQuery(strResponse.mResult);
				}
			}
			//submit order begin
			else if(response.mStep == STEP_SUBMIT_ORDER_CHECK_USER){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//StringHttpResponse strResponse = (StringHttpResponse)response;
					stepSubmitOrderRequest();
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_REQUEST){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//StringHttpResponse strResponse = (StringHttpResponse)response;
					stepSubmitOrderRequest();
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_INITDC){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//StringHttpResponse strResponse = (StringHttpResponse)response;
					
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_GET_CAPTCHA){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					//StringHttpResponse strResponse = (StringHttpResponse)response;
					
				}
			}
			//submit order end
		}
	}
}
