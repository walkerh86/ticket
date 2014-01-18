import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	
	private boolean mQueryStart = false;
	
	public ProcessMainQuery(UiInterface cb, BlockingQueue<MyHttpUrlRequest> queue, 
			UserInfo userInfo, PassengerManager passengerManager){	
		mCallBack = cb;
		mRequestQueue = queue;
		mUserInfo = userInfo;
		mPassengerManager = passengerManager;
		
		initUi();
	}
	
	private void initUi(){
		mFrameMain = new FrameMain(mUserInfo,this);
		mFrameMain.setVisible(true);
	}
	
	@Override
	public void onUiAction(int action){
		if(action == UiActionListener.UI_ACTION_TICKET_AUTO_QUERY_START){
			if(!mQueryStart){
				mQueryStart = true;
				stepQueryLeft();
			}
		}else if(action == UI_ACTION_TICKET_AUTO_QUERY_END){
			mQueryStart = false;
		}
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
	
	public void parseTicketQuery(String str){
		Log.i("parseTicketQuery start");
		Log.i("parseTicketQuery,str="+str);
		
		JSONObject jObj = JSONObject.fromObject(str);
		String jString = jObj.getString("data");
		
		JSONArray trainList = JSONArray.fromObject(jString);//jsonObj.getJSONArray("data");		
		JSONObject train = getBestTrain(trainList);
		if(train != null && mBestSeatType != null){
			TicketInfo tickInfo = TicketInfo.getTicketInfoFromJSONObject(train);
			tickInfo.setSeatType(mBestSeatType);
			Log.i("parseTicketQuery,ticketInfo="+tickInfo.toString());
			submitOrder(tickInfo);
			
			mQueryStart = false;
			mFrameMain.setQueryState(mQueryStart);
		}else if(mQueryStart){
			stepQueryLeft();
		}
		Log.i("===================================================================");
		Log.i("parseTicketQuery,bestTrain="+train);
		Log.i("parseTicketQuery end");
	}
	
	private ProcessSubmitOrder mProcessSubmitOrder;
	private void submitOrder(TicketInfo tickInfo){
		mPassengerNum = mPassengerManager.getSelectedPassengers().size();
		mProcessSubmitOrder = new ProcessSubmitOrder(null,mRequestQueue,mUserInfo,mPassengerManager);
		mProcessSubmitOrder.startSubmitOrderSequence(tickInfo);
	}

	private String mBestSeatType = null;
	private JSONObject getBestTrain(JSONArray trainList){
		JSONObject bestTrain = null;
		String bestSeatType = null;
		int bestWeight = 0;
		ArrayList<String> seatFilter = mUserInfo.getSeatFitler();
		ArrayList<String> trainFilter = mUserInfo.getTrainFitler();
		String priorityType = mUserInfo.getPriorityType();
		int seatWeightMax = seatFilter.size();
		int trainWeightMax = trainFilter.size();
		int weightMax = seatWeightMax*trainWeightMax;
		int count = trainList.size();
		JSONObject train = null;
		int trainWeight = 0;
		int seatWeight = 0;
		int finalWeight = 0;
				
		for(int i=0;i<count;i++){
			train = trainList.getJSONObject(i).getJSONObject("queryLeftNewDTO");			
			trainWeight = getTrainWeightValue(train,trainFilter);
			seatWeight = getSeatWeightValue(train,seatFilter);	
			
			if(priorityType.equals(UserInfo.PRIORITY_TYPE_SEAT)){
				finalWeight = (trainWeight-1)*trainWeightMax + seatWeight;
			}else if(priorityType.equals(UserInfo.PRIORITY_TYPE_TRAIN)){
				finalWeight = (seatWeight-1)*seatWeightMax + trainWeight;
			}else{
				finalWeight = seatWeight*trainWeight;
			}
			if(trainWeight > 0){
				Log.i("getBestTrain,train="+train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE)
						+",swz_num="+train.getString(TicketInfoConstants.KEY_SWZ_NUM)
						+",tz_num="+train.getString(TicketInfoConstants.KEY_TZ_NUM)
						+",zy_num="+train.getString(TicketInfoConstants.KEY_ZY_NUM)
						+",ze_num="+train.getString(TicketInfoConstants.KEY_ZE_NUM));
				Log.i("getBestTrain,trainWeight="+trainWeight+",seatWeight="+seatWeight);
			}
			if(finalWeight > bestWeight){
				bestWeight = finalWeight;
				bestTrain = trainList.getJSONObject(i);//train;
				bestSeatType = getSeatTypeByWeight(seatWeight,seatFilter);
				Log.i("bestSeatType="+bestSeatType);
				if(bestWeight == weightMax){
					break;
				}
			}
		}
		
		mBestSeatType = bestSeatType;
		return bestTrain;
	}
	
	private int getTrainWeightValue(JSONObject train, ArrayList<String> trainFilter){
		int weightValue = 0;
		int count = trainFilter.size();
		String trainCode = null;
		for(int i=0,weight=count;i<count;i++,weight--){
			trainCode = train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
			if(trainCode.equals(trainFilter.get(i))){
				weightValue = weight;
				break;
			}
		}
		return weightValue;
	}
	
	private static final int ENOUGH_SEAT_NUM = 100;
	private int mPassengerNum = 9;
	
	private int getSeatWeightValue(JSONObject train, ArrayList<String> seatFilter){
		int weightValue = 0;
		int count = seatFilter.size();
		String seatNum = null;
		int num = 0;
		for(int i=0,weight=count;i<count;i++,weight--){
			String numKey = SeatInfo.getSeatNumKey(seatFilter.get(i));
			seatNum = train.getString(SeatInfo.getSeatNumKey(seatFilter.get(i)));
			num = checkSeatNum(seatNum);
			if(num >= mPassengerNum){
				weightValue = weight;
				break;
			}
		}
		return weightValue;
	}
		
	private String getSeatTypeByWeight(int weight, ArrayList<String> seatFilter){
		String seatType = null;
		int count = seatFilter.size();
		int index = count-weight;
		if(index >=0 && index < count){
			seatType = seatFilter.get(index);
		}
		return seatType;
	}
	
	private int checkSeatNum(String seatNum){
		int num = 0;
		if(seatNum.equals("��")){
			num = ENOUGH_SEAT_NUM;
		}else if(seatNum.equals("��") || seatNum.equals("--") || seatNum.equals("*")){
			num = 0;
		}else{
			num = Integer.valueOf(seatNum);
		}
		return num;
	}
}