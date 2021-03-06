import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Log;
import util.TicketInfoConstants;
import util.UrlConstants;
import net.CookieManager;
import net.HttpHeader;
import net.HttpResponseHandler;
import net.ImageHttpResponse;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StringHttpResponse;
import net.sf.json.JSONObject;


public class ProcessSubmitOrder implements HttpResponseHandler,UiActionListener{
	public static final int STEP_SUBMIT_ORDER_CHECK_USER = 10;
	public static final int STEP_SUBMIT_ORDER_REQUEST = 11;
	public static final int STEP_SUBMIT_ORDER_INITDC = 12;
	public static final int STEP_SUBMIT_ORDER_GET_CAPTCHA = 13;
	public static final int STEP_SUBMIT_ORDER_CHECK_ORDER_INFO = 14;
	public static final int STEP_SUBMIT_ORDER_GET_QUEUE_COUNT = 15;
	public static final int STEP_SUBMIT_ORDER_CONFIRM_SINGLE = 16;
	
	private Object mLock = new Object();
	private UiInterface mCallBack;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	private PassengerManager mPassengerManager;
	
	private TicketInfo mSubmitTicketInfo = null;
	private String mSubmitToken = null;
	private String mKeyCheckIsChange = null;
	
	private FrameSubmitOrder mFrameSubmitOrder;
	
	private ProcessOrderList mProcessOrderList;
	
	public ProcessSubmitOrder(UiInterface cb, BlockingQueue<MyHttpUrlRequest> queue, 
			UserInfo userInfo, PassengerManager passengerManager){
		mCallBack = cb;
		mRequestQueue = queue;
		mUserInfo = userInfo;
		mPassengerManager = passengerManager;
	}	
	
	public void startSubmitOrderSequence(TicketInfo tickInfo){
		mSubmitTicketInfo = tickInfo;
		
		stepSubmitOrderCheckUser(this);		
		initUi();
	}
	
	public void initUi(){
		if(mFrameSubmitOrder == null){
			mFrameSubmitOrder = new FrameSubmitOrder(this);
		}
		
		mFrameSubmitOrder.showNewSubmitOrder(mSubmitTicketInfo);		
		mFrameSubmitOrder.setVisible(true);
	}
	
	@Override
	public void onUiAction(int action){
		if(action == UiActionListener.UI_ACTION_TICKET_SUBMIT){
			stepSubmitOrderCheckOrderInfo(this);
		}else if(action == UiActionListener.UI_ACTION_UPDATE_CAPTCHA){
			stepSubmitOrderGetCaptCha(this);
		}else if(action == UiActionListener.UI_ACTION_ORDER_QUERY_NO_COMPLETE){
			if(mProcessOrderList == null){
				mProcessOrderList = new ProcessOrderList(mRequestQueue);
			}
			mProcessOrderList.showOrderList();
		}
	}
	
	public void stepSubmitOrderCheckUser(HttpResponseHandler handler){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att","");
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/login/checkUser","POST",
				HttpHeader.getHeader(UrlConstants.REF_TICKET_URL),params,
				new StringHttpResponse(handler,STEP_SUBMIT_ORDER_CHECK_USER)));
	}
	
	public void stepSubmitOrderRequest(HttpResponseHandler handler){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("secretStr",mSubmitTicketInfo.mSecretStr);
		params.put("train_date",mUserInfo.getDate());
		params.put("back_train_date",mUserInfo.getDate());
		params.put("tour_flag","dc");
		params.put("purpose_codes","ADULT");
		params.put("query_from_station_name",mSubmitTicketInfo.mFromStationName);
		params.put("query_to_station_name",mSubmitTicketInfo.mToStationName);
		params.put("undefined","");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_SUBMITORDER_URL,"POST",
				HttpHeader.getHeader(UrlConstants.REF_TICKET_URL),params,
				new StringHttpResponse(handler,STEP_SUBMIT_ORDER_REQUEST)));
	}
	
	public void stepSubmitOrderInitDc(HttpResponseHandler handler){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att","");
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_INITDC_URL,"POST",
				HttpHeader.getHeader(UrlConstants.REF_TICKET_URL),params,
				new StringHttpResponse(handler,STEP_SUBMIT_ORDER_INITDC)));
	}
	
	public void stepSubmitOrderGetCaptCha(HttpResponseHandler handler){
		mRequestQueue.add(new MyHttpUrlRequest(UrlConstants.REQ_GETSUBPASSCODE_URL,"GET",
				HttpHeader.getHeader(UrlConstants.REQ_INITDC_URL),null,
				new ImageHttpResponse(UrlConstants.FILE_SUBMIT_CAPTCHA_URL,handler,STEP_SUBMIT_ORDER_GET_CAPTCHA)));
	}
	
	public void stepSubmitOrderCheckOrderInfo(HttpResponseHandler handler){
		try{
			Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("cancel_flag", "2");
			params.put("bed_level_order_num", "000000000000000000000000000000");
			params.put("passengerTicketStr", URLEncoder.encode(getPassengerTicketStr(),"UTF-8"));
			params.put("oldPassengerStr", URLEncoder.encode(getOldPassengerStr(),"UTF-8"));
			params.put("tour_flag", "dc");
			params.put("randCode", mFrameSubmitOrder.getCaptchaCode());
			params.put("_json_att", "");		
			params.put("REPEAT_SUBMIT_TOKEN", mSubmitToken);		
			mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo","POST",
					HttpHeader.getHeader(UrlConstants.REQ_INITDC_URL),params,
					new StringHttpResponse(handler,STEP_SUBMIT_ORDER_CHECK_ORDER_INFO)));
		}catch(UnsupportedEncodingException e){
			Log.i("stepSubmitOrderCheckOrderInfo,e="+e);
		}
	}
	
	public void stepSubmitOrderGetQueueCount(HttpResponseHandler handler){
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("train_date", "Fri+Jan+17+00%3A00%3A00+UTC%2B0800+2014");
		params.put("train_no", mSubmitTicketInfo.mTrainNo);
		params.put("stationTrainCode", mSubmitTicketInfo.mStationTrainCode);
		params.put("seatType", mSubmitTicketInfo.mSeatTypeCode);
		params.put("fromStationTelecode", mSubmitTicketInfo.mFromStationTelecode);
		params.put("toStationTelecode", mSubmitTicketInfo.mToStationTelecode);
		params.put("leftTicket", mSubmitTicketInfo.mYpInfo);
		params.put("purpose_codes", "00");		
		params.put("_json_att", "");
		params.put("REPEAT_SUBMIT_TOKEN", mSubmitToken);
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount","POST",
				HttpHeader.getHeader(UrlConstants.REQ_INITDC_URL),params,
				new StringHttpResponse(handler,STEP_SUBMIT_ORDER_GET_QUEUE_COUNT)));
	}
	
	public void stepSubmitOrderConfirmSingle(HttpResponseHandler handler){
		try{
			Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("passengerTicketStr", URLEncoder.encode(getPassengerTicketStr(),"UTF-8"));
			params.put("oldPassengerStr", URLEncoder.encode(getOldPassengerStr(),"UTF-8"));
			params.put("randCode", mFrameSubmitOrder.getCaptchaCode());
			params.put("purpose_codes", "00");
			params.put("key_check_isChange", mKeyCheckIsChange);
			params.put("leftTicketStr", mSubmitTicketInfo.mYpInfo);
			params.put("train_location", mSubmitTicketInfo.mLocationCode);
			params.put("_json_att", "");
			params.put("REPEAT_SUBMIT_TOKEN", mSubmitToken);
			mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue","POST",
					HttpHeader.getHeader(UrlConstants.REQ_INITDC_URL),params,
					new StringHttpResponse(handler,STEP_SUBMIT_ORDER_CONFIRM_SINGLE)));
		}catch(UnsupportedEncodingException e){
			Log.i("stepSubmitOrderConfirmSingle,e="+e);
		}
	}
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){		
			Log.i("handleResponse,mStep ="+response.mStep);
			if(response.mStep == STEP_SUBMIT_ORDER_CHECK_USER){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					Log.i(strResponse.mResult);
					stepSubmitOrderRequest(this);
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_REQUEST){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;	
					parseSubmitOrderRequest(strResponse.mResult);
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_INITDC){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					parseInitDcResponse(strResponse.mResult);
					stepSubmitOrderGetCaptCha(this);
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_GET_CAPTCHA){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					ImageHttpResponse imgResponse = (ImageHttpResponse)response;					
					mFrameSubmitOrder.setCaptchaIcon(imgResponse.mResult);
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_CHECK_ORDER_INFO){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;					
					parseCheckInfoResponse(strResponse.mResult);
					//stepSubmitOrderGetQueueCount(this);
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_GET_QUEUE_COUNT){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					parseGetQueueResponse(strResponse.mResult);
					stepSubmitOrderConfirmSingle(this);
				}
			}else if(response.mStep == STEP_SUBMIT_ORDER_CONFIRM_SINGLE){
				Log.i("msg="+response.mResponseMsg+",code="+response.mResponseCode);
				if(response.mResponseCode == 200){
					StringHttpResponse strResponse = (StringHttpResponse)response;
					parseConfimSingleResponse(strResponse.mResult);					
				}
			}
		}
	}
	
	private void parseSubmitOrderRequest(String response){
		Log.i("parseSubmitOrderRequest,response="+response);
		JSONObject jObj = JSONObject.fromObject(response);
		String message = jObj.getString("messages");
		Log.i("parseSubmitOrderRequest,messge="+message);
		if(message.equals("[]")){
			stepSubmitOrderInitDc(this);
		}else{
			mFrameSubmitOrder.showLog(message);
		}
	}
	
	private void parseConfimSingleResponse(String response){
		Log.i("parseConfimSingle, response="+response);
		JSONObject jObj = JSONObject.fromObject(response);
		JSONObject jData = jObj.getJSONObject("data");
		String submitStatus = jData.getString("submitStatus");
		if(submitStatus.equals("true")){
			mFrameSubmitOrder.showLog("��Ʊ�ɹ�����鶩����");
		}else{
			String errMsg = jData.getString("errMsg");
			mFrameSubmitOrder.showLog(errMsg);
		}
	}
	
	private void parseGetQueueResponse(String response){
		Log.i("parseGetQueue, msg="+response);
	}
	
	private void parseCheckInfoResponse(String response){
		Log.i("parseCheckInfo, msg="+response);
		JSONObject jObj = JSONObject.fromObject(response);
		JSONObject jData = jObj.getJSONObject("data");
		String submitStatus = jData.getString("submitStatus");
		if(submitStatus.equals("true")){
			stepSubmitOrderGetQueueCount(this);
		}else{
			String errMsg = jData.getString("errMsg");
			mFrameSubmitOrder.showLog(errMsg);
		}
	}
	
	private void parseInitDcResponse(String response){
		//Log.i("parseInitDcResponse, msg="+msg);
		Matcher m_token = getMatcher("var globalRepeatSubmitToken = '(\\w+)'", response);
		Matcher m_key_check_isChange = getMatcher("'key_check_isChange':'(\\w+)'", response);
		if(m_token.find()){
			mSubmitToken = m_token.group(1);
		}
		if(m_key_check_isChange.find()){
			mKeyCheckIsChange = m_key_check_isChange.group(1);
		}
		Log.i("parseInitDcResponse,mSubmitToken="+mSubmitToken+",mKeyCheckIsChange="+mKeyCheckIsChange);
	}
	
	private Matcher getMatcher(String regex, String targetStr) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(targetStr);
		return m;
	}
	
	public String getPassengerTicketStr() {
		String oldStrs = "";
		HashMap<String,Passenger> passengers = mPassengerManager.getSelectedPassengers();
		Passenger passenger;
		Log.i("seatTypeCode="+mSubmitTicketInfo.mSeatTypeCode);
		int passengerNum = 0;
		for(Map.Entry<String, Passenger>entry : passengers.entrySet()){
			passenger = entry.getValue();
			String oldStr = "";
			if ("wz".equals(mSubmitTicketInfo.mSeatTypeCode)) {
			} else {
				oldStr = mSubmitTicketInfo.mSeatTypeCode;
			}
			String bR = oldStr + ",0," + passenger.getTypeCode() + "," + passenger.getName() + "," 
					+ passenger.getIdTypeCode() + "," + passenger.getIdNo() + ","
					+ passenger.getMobileNo() + ",N";
			oldStrs += bR + "_";
			
			passengerNum++;
			if(passengerNum >= mSubmitTicketInfo.mSeatTicketNum){
				break;
			}
		}
		
		return oldStrs.substring(0, oldStrs.length() - 1);
	}
	
	public String getOldPassengerStr() {
		String oldStrs = "";
		HashMap<String,Passenger> passengers = mPassengerManager.getSelectedPassengers();
		Passenger passenger;
		int passengerNum = 0;
		for(Map.Entry<String, Passenger>entry : passengers.entrySet()){
			passenger = entry.getValue();
			String oldStr = passenger.getName() + "," + passenger.getIdTypeCode() + "," 
					+ passenger.getIdNo() + "," + passenger.getTypeCode();
			oldStrs += oldStr + "_";
			
			passengerNum++;
			if(passengerNum >= mSubmitTicketInfo.mSeatTicketNum){
				break;
			}
		}
		return oldStrs;
	}
}
