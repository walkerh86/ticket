import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import javax.swing.ImageIcon;

import util.Log;

import net.HttpDispatcher;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class MainProcess implements UiInterface{
	private FrameLogin mLoginFrame;
	private FrameMain mMainFrame;
	private RequestProcess mTicketProcess;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	
	public void init(BlockingQueue<MyHttpUrlRequest> queue){
		mUserInfo = new UserInfo();
		mRequestQueue = queue;
		mTicketProcess = new RequestProcess(mRequestQueue,this,mUserInfo);
		mTicketProcess.stepGetCookie();	
				
		mLoginFrame = new FrameLogin(); 
		mLoginFrame.setVisible(true); 
		mLoginFrame.setOnLogInListener(new FrameLogin.OnLogInListener() {			
			@Override
			public void OnLogIn() {
				mTicketProcess.stepLoginAyncSuggest();
			}
		});
		mLoginFrame.setUserInfo(mUserInfo);
	}
	
	@Override
	public void setLoginCaptcha(ImageIcon icon){
		mLoginFrame.setCaptchaIcon(icon);
	}
	
	@Override
	public void loginSuccess(){
		mLoginFrame.setVisible(false); 
		if(mMainFrame == null){
			mMainFrame = new FrameMain(mUserInfo);
			mMainFrame.setTickProcess(mTicketProcess);
		}
		mMainFrame.setVisible(true);
	}
	
	public static final String KEY_STATION_TRAIN_CODE = "station_train_code";
	public static final String KEY_START_STATION_TELECODE = "start_station_telecode";
	public static final String KEY_START_STATION_NAME = "start_station_name";
	public static final String KEY_END_STATION_TELECODE = "end_station_telecode";
	public static final String KEY_END_STATION_NAME = "end_station_name";
	public static final String KEY_FROM_STATION_TELECODE = "from_station_telecode";
	public static final String KEY_FROM_STATION_NAME = "from_station_name";
	public static final String KEY_TO_STATION_TELECODE = "to_station_telecode";
	public static final String KEY_TO_STATION_NAME = "to_station_name";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_ARRIVE_TIME = "arrive_time";
	public static final String KEY_DAY_DIFFERENCE = "day_difference";
	public static final String KEY_LAST_TIME = "lishi";
	public static final String KEY_START_TRAIN_DATE = "start_train_date";	
	//public static final String KEY_GG_NUM = "gg_num";	
	public static final String KEY_SWZ_NUM = "swz_num";
	public static final String KEY_TZ_NUM = "tz_num";
	public static final String KEY_ZY_NUM = "zy_num";
	public static final String KEY_ZE_NUM = "ze_num";
	public static final String KEY_GR_NUM = "gr_num";
	public static final String KEY_RW_NUM = "rw_num";
	public static final String KEY_YW_NUM = "yw_num";
	public static final String KEY_RZ_NUM = "rz_num";
	public static final String KEY_YZ_NUM = "yz_num";
	public static final String KEY_WZ_NUM = "wz_num";
	public static final String KEY_QT_NUM = "qt_num";
	
	public static final HashMap<String,String> mSeats = new HashMap<String,String>();
	static{
		mSeats.put("swz_num","商务座");
		mSeats.put("tz_num","特等座");
		mSeats.put("zy_num","一等座");
		mSeats.put("ze_num","二等座");
		mSeats.put("gr_num","高级软卧");
		mSeats.put("rw_num","软卧");
		mSeats.put("yw_num","硬卧");
		mSeats.put("rz_num","软座");	
		mSeats.put("yz_num","硬座");
		mSeats.put("wz_num","无座");
		mSeats.put("qt_num","其他");
	}
	
	@Override
	public void parseTicketQuery(String str){
		Log.i("parseTicketQuery start");
		JSONObject jsonObj = JSONObject.fromObject(str);
		JSONArray trainList = jsonObj.getJSONArray("data");		
		int count = trainList.size();		
		for(int i=0;i<count;i++){
			JSONObject train = trainList.getJSONObject(i).getJSONObject("queryLeftNewDTO");
			Log.i(train.toString());
		}
		Log.i("parseTicketQuery end");
	}
	/*
	private ArrayList<JSONObject> getFilterTrainList(JSONArray srcList, HashSet ){
		ArrayList<JSONObject> dstList = new ArrayList<JSONObject>(20);
		HashSet seatFilter = mUserInfo.getSeatFitler();
		HashSet trainFilter = mUserInfo.getTrainFitler();
		return dstList;
	}
	*/
}
