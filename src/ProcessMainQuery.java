import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ProcessMainQuery implements HttpResponseHandler,UiActionListener{
	public static final int STEP_QUERY_LEFT = 6;
	
	private Object mLock = new Object();
	private UiInterface mCallBack;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	private PassengerManager mPassengerManager;	
	
	private FrameMain mFrameMain;
	
	private boolean mAutoQueryStart = false;
	private boolean mQueryAuto = false;
	private boolean mSubmitWithoutEnoughTicket = false;
	HashSet<String> mTrainTypeFilters = new HashSet<String>();
	
	public ProcessMainQuery(UiInterface cb, BlockingQueue<MyHttpUrlRequest> queue, 
			UserInfo userInfo, PassengerManager passengerManager){	
		mCallBack = cb;
		mRequestQueue = queue;
		mUserInfo = userInfo;
		mPassengerManager = PassengerManager.getInstance();
		
		initUi();
	}
	
	private void initUi(){
		mFrameMain = new FrameMain(mUserInfo,this);
		mFrameMain.setVisible(true);
	}
	
	@Override
	public void onUiAction(int action){
		if(action == UiActionListener.UI_ACTION_TICKET_QUERY){
			if(mAutoQueryStart){
				mAutoQueryStart = false;
				mFrameMain.setQueryState(mAutoQueryStart);
			}else{
				mPassengerNum = mPassengerManager.getSelectedPassengers().size();
				mTrainTypeFilters = mFrameMain.getTrainTypeFilter();
				mQueryAuto = mUserInfo.getQueryAuto().equals("true") ? true : false;
				mSubmitWithoutEnoughTicket = mUserInfo.getSubmitWithoutEnoughTicket().equals("true") ? true : false;
				if(mQueryAuto){
					mAutoQueryStart = true;
				}
				mFrameMain.setQueryState(mAutoQueryStart);
				stepQueryLeft();
			}
		}/*else if(action == UI_ACTION_TICKET_AUTO_QUERY_END){
			mAutoQueryStart = false;
		}*/
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
			Log.i("handleResponse,mStep ="+response.mStep);
			if(response.mResponseCode == 200){
				StringHttpResponse strResponse = (StringHttpResponse)response;
				//Log.i(strResponse.mResult);
				parseTicketQuery(strResponse.mResult);
			}
		}
	}
	
	public class TimerQueryTask extends TimerTask{
	    public void run(){
	    	stepQueryLeft();
	    }
	}
	private TimerQueryTask mTimerQueryTask = new TimerQueryTask();
	Timer mTimer = new Timer();
	
	public void parseTicketQuery(String str){
		Log.i("parseTicketQuery start");
		Log.i("parseTicketQuery,str="+str);
		
		JSONObject jObj = JSONObject.fromObject(str);
		String jString = jObj.getString("data");
		
		JSONArray trainList = JSONArray.fromObject(jString);
		JSONObject train = getBestTrain(trainList);
		if(mAutoQueryStart && train != null && mBestSeatType != null){
			TicketInfo tickInfo = TicketInfo.getTicketInfoFromJSONObject(train,mBestSeatType);
			Log.i("parseTicketQuery,ticketInfo="+tickInfo.toString());
			submitOrder(tickInfo);
			
			mAutoQueryStart = false;
			mFrameMain.setQueryState(mAutoQueryStart);
		}else if(mAutoQueryStart){
			//stepQueryLeft();
			mTimer.schedule(new TimerQueryTask(), 50);
		}
		Log.i("===================================================================");
		Log.i("parseTicketQuery,bestTrain="+train);
		Log.i("parseTicketQuery,bestseatType="+mBestSeatType);
		Log.i("parseTicketQuery end");
	}
	
	private ProcessSubmitOrder mProcessSubmitOrder;
	private void submitOrder(TicketInfo tickInfo){		
		mProcessSubmitOrder = new ProcessSubmitOrder(null,mRequestQueue,mUserInfo,mPassengerManager);
		mProcessSubmitOrder.startSubmitOrderSequence(tickInfo);
	}

	private String mBestSeatType = null;
	private static final String[] mDefaultSeatFilter = {
		SeatInfo.KEY_ZE,
		SeatInfo.KEY_YW,
		SeatInfo.KEY_YZ,
		
		SeatInfo.KEY_RW,
		SeatInfo.KEY_RZ,
		SeatInfo.KEY_GR,
		
		SeatInfo.KEY_ZY,
		//SeatInfo.KEY_TZ,
		//SeatInfo.KEY_SW,
		SeatInfo.KEY_WZ
	};
	private JSONObject getBestTrain(JSONArray trainList){
		JSONObject bestTrain = null;
		String[] seatFilter = mUserInfo.getSeatFitlerArray();
		if(seatFilter == null || seatFilter.length == 0){			
			seatFilter = mDefaultSeatFilter;
		}
		String[] trainFilter = mUserInfo.getTrainFitlerArray();
		String priorityType = mUserInfo.getPriorityType();
		int seatWeightMax = (seatFilter != null) ? seatFilter.length : 0;
		int trainWeightMax = (trainFilter != null) ? trainFilter.length : 0;
		int weightMax = 0;
		int count = trainList.size();
		JSONObject train = null;
		int trainWeight = 0;
		int seatWeight = 0;
		int finalWeight = 0;
		int bestWeight = 0;
		int bestSeatWeight = 0;
				
		if(trainFilter != null){
			weightMax = seatWeightMax*trainWeightMax;
			if(priorityType.equals(UserInfo.PRIORITY_TYPE_SEAT)){
				weightMax += seatWeightMax;
			}else if(priorityType.equals(UserInfo.PRIORITY_TYPE_TRAIN)){
				weightMax += trainWeightMax;
			}
		}else{
			weightMax = seatWeightMax;
		}
		String logStr = "";
		for(int i=0;i<count;i++){
			train = trainList.getJSONObject(i).getJSONObject("queryLeftNewDTO");
			String trainCode = train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
			if(checkTrainCodeFilter(trainCode)){
				continue;
			}
			
			if(trainFilter != null){
				trainWeight = getTrainWeightValue(train,trainFilter);
				seatWeight = getSeatWeightValue(train,seatFilter);	
				if(priorityType.equals(UserInfo.PRIORITY_TYPE_SEAT)){
					finalWeight = (seatWeight-1)*trainWeightMax + seatWeight+trainWeight;
				}else if(priorityType.equals(UserInfo.PRIORITY_TYPE_TRAIN)){
					finalWeight = (trainWeight-1)*seatWeightMax + trainWeight+seatWeight;
				}else{
					finalWeight = seatWeight*trainWeight;
				}
			}else{
				seatWeight = getSeatWeightValue(train,seatFilter);	
				finalWeight = seatWeight;
			}
			
			if(!mAutoQueryStart || finalWeight > 0){
				logStr += train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE)
						+",  "+SeatInfo.getSeatName("zy")+"="+train.getString(TicketInfoConstants.KEY_ZY_NUM)
						+",  "+SeatInfo.getSeatName("ze")+"="+train.getString(TicketInfoConstants.KEY_ZE_NUM)
						+",  "+SeatInfo.getSeatName("rw")+"="+train.getString(TicketInfoConstants.KEY_RW_NUM)
						+",  "+SeatInfo.getSeatName("yw")+"="+train.getString(TicketInfoConstants.KEY_YW_NUM)
						+",  "+SeatInfo.getSeatName("rz")+"="+train.getString(TicketInfoConstants.KEY_RZ_NUM)
						+",  "+SeatInfo.getSeatName("yz")+"="+train.getString(TicketInfoConstants.KEY_YZ_NUM)
						+"\n";
				Log.i("getBestTrain,trainCode="+trainCode+",trainWeight="+trainWeight+",seatWeight="+seatWeight
						+",weightMax="+weightMax+",finalWeight="+finalWeight+",priorityType="+priorityType);
			}
			if(finalWeight > bestWeight){
				bestWeight = finalWeight;
				bestTrain = trainList.getJSONObject(i);//train;
				bestSeatWeight = seatWeight;
				if(bestWeight == weightMax){
					break;
				}
			}
		}
		
		mFrameMain.showLog(logStr);
		mBestSeatType = getSeatTypeByWeight(bestSeatWeight,seatFilter);
		return bestTrain;
	}
		
	private int getTrainWeightValue(JSONObject train, String[] trainFilter){
		int weightValue = 0;
		int count = trainFilter.length;
		String trainCode = null;
		for(int i=0,weight=count;i<count;i++,weight--){
			trainCode = train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
			if(trainCode.equals(trainFilter[i])){
				weightValue = weight;
				break;
			}
		}
		return weightValue;
	}
		
	private int mPassengerNum = 9;
	
	private int getSeatWeightValue(JSONObject train, String[] seatFilter){
		int weightValue = 0;
		int count = seatFilter.length;
		String seatNum = null;
		int num = 0;
		for(int i=0,weight=count;i<count;i++,weight--){
			//String numKey = SeatInfo.getSeatNumKey(seatFilter[i]);
			seatNum = train.getString(SeatInfo.getSeatNumKey(seatFilter[i]));
			num = SeatInfo.checkSeatNum(seatNum);
			if(/*mSubmitWithoutEnoughTicket || */num >= mPassengerNum){
				weightValue = weight;
				break;
			}
		}
		return weightValue;
	}
		
	private String getSeatTypeByWeight(int weight, String[] seatFilter){
		String seatType = null;
		int count = seatFilter.length;
		int index = count-weight;
		if(index >=0 && index < count){
			seatType = seatFilter[index];
		}
		return seatType;
	}
	
	private boolean checkTrainCodeFilter(String trainCode){
		if(mTrainTypeFilters.size() == 0){
			return false;
		}
		boolean filtered = false;
		String type = trainCode.substring(0, 1);
		if(type.equals(UserInfo.TRAIN_TYPE_FILTER_G) 
			|| type.equals(UserInfo.TRAIN_TYPE_FILTER_D)
			|| type.equals(UserInfo.TRAIN_TYPE_FILTER_Z)
			|| type.equals(UserInfo.TRAIN_TYPE_FILTER_T)
			|| type.equals(UserInfo.TRAIN_TYPE_FILTER_K)){
			
		}else{
			type = "Q";
		}
		if(!mTrainTypeFilters.contains(type)){
			filtered = true;
		}
		return filtered;
	}
}
