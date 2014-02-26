import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
	//HashSet<String> mTrainTypeFilters = new HashSet<String>();
	
	private int mPassengerNum = 9;
	
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
				String seatFilter = mUserInfo.getSeatFitler();
				if(seatFilter == null || seatFilter.length() == 0){
					mSeatFilter.setSeatFilters(mDefaultSeatFilter);
				}else{
					mSeatFilter.setSeatFilters(mUserInfo.getSeatFitler());
				}		
				mTrainCodeFilter.setTrainCodeFilters(mUserInfo.getTrainFilter(),
						mUserInfo.getConsiderOtherTrain());				
				mPassengerNum = mPassengerManager.getSelectedPassengers().size();
				//mTrainTypeFilters = mFrameMain.getTrainTypeFilter();
				mTrainTypeFilter.setTrainTypeFilters(mUserInfo.getTrainTypeFilter());
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
	//private TimerQueryTask mTimerQueryTask = new TimerQueryTask();
	Timer mTimer = new Timer();
	
	public void parseTicketQuery(String str){
		Log.i("parseTicketQuerystart str="+str);
		
		JSONObject jObj = JSONObject.fromObject(str);
		String jString = null;
		try{
			jString = jObj.getString("data");
		}catch(net.sf.json.JSONException e){
			String errMsg = jObj.getString("messages");
			mFrameMain.showLog(errMsg);
			return;
		}
		
		JSONArray trainList = JSONArray.fromObject(jString);
		JSONObject train = getBestTrain(trainList);
		if(mAutoQueryStart && train != null){
			String bestSeat = mSeatFilter.getTrainBestSeat(train);
			Log.i("parseTicketQuery,bestseatType="+bestSeat);
			TicketInfo tickInfo = TicketInfo.getTicketInfoFromJSONObject(train,bestSeat);
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
		Log.i("parseTicketQuery,bestSeat="+mSeatFilter.getTrainBestSeat(train));
		Log.i("parseTicketQuery end");
	}
	
	private ProcessSubmitOrder mProcessSubmitOrder;
	private void submitOrder(TicketInfo tickInfo){		
		if(mProcessSubmitOrder == null){
			mProcessSubmitOrder = new ProcessSubmitOrder(null,mRequestQueue,mUserInfo,mPassengerManager);
		}
		mProcessSubmitOrder.startSubmitOrderSequence(tickInfo);
	}

	private static final String mDefaultSeatFilter = 
			SeatInfo.KEY_ZE
			+","+SeatInfo.KEY_YW
			+","+SeatInfo.KEY_YZ
			+","+SeatInfo.KEY_RW
			+","+SeatInfo.KEY_RZ
			+","+SeatInfo.KEY_GR
			+","+SeatInfo.KEY_ZY
			+","+SeatInfo.KEY_WZ;
	
	private JSONObject getBestTrain(JSONArray trainList){
		JSONObject bestTrain = null;
					
		String priorityType = mUserInfo.getPriorityType();
		int seatWeightMax = mSeatFilter.getMaxWeight();
		int trainWeightMax = mTrainCodeFilter.getMaxWeight();
		int weightMax = 0;
		int count = trainList.size();
		JSONObject train = null;
		int trainWeight = 0;
		int seatWeight = 0;
		int finalWeight = 0;
		int bestWeight = 0;
				
		if(mTrainCodeFilter.applayFilter()){
			weightMax = seatWeightMax*trainWeightMax;
			if(priorityType.equals(UserInfo.PRIORITY_TYPE_SEAT)){
				weightMax += seatWeightMax;
			}else if(priorityType.equals(UserInfo.PRIORITY_TYPE_TRAIN)){
				weightMax += trainWeightMax;
			}
		}else{
			weightMax = seatWeightMax;
		}
		
		ArrayList<JSONObject> trainListArray = new ArrayList<JSONObject>();
		for(int i=0;i<count;i++){
			train = trainList.getJSONObject(i).getJSONObject("queryLeftNewDTO");
			String trainCode = train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
			
			if(!mTrainCodeFilter.isFiltered(trainCode) && !mTrainTypeFilter.isFiltered(trainCode)){
				trainListArray.add(train);
			}else{
				continue;
			}
			
			if(mSeatFilter.isFiltered(train)){
				continue;
			}
			
			if(mTrainCodeFilter.applayFilter()){
				trainWeight = mTrainCodeFilter.getTrainCodeWeight(trainCode);
				seatWeight = mSeatFilter.getSeatWeight(trainCode);	
				if(priorityType.equals(UserInfo.PRIORITY_TYPE_SEAT)){
					finalWeight = (seatWeight-1)*trainWeightMax + seatWeight+trainWeight;
				}else if(priorityType.equals(UserInfo.PRIORITY_TYPE_TRAIN)){
					finalWeight = (trainWeight-1)*seatWeightMax + trainWeight+seatWeight;
				}else{
					finalWeight = seatWeight*trainWeight;
				}
			}else{
				finalWeight = mSeatFilter.getSeatWeight(trainCode);
			}
			
			if(finalWeight > bestWeight){
				bestWeight = finalWeight;
				bestTrain = trainList.getJSONObject(i);
				if(bestWeight == weightMax){
					break;
				}
			}
		}
		
		if(mTrainCodeFilter.applayFilter()){
			Collections.sort(trainListArray,mWeightComparator);
		}
		mFrameMain.updateTrainListTable(trainListArray);
		return bestTrain;
	}
		
	private TrainTypeFilter mTrainTypeFilter = new TrainTypeFilter();
	private class TrainTypeFilter{
		private HashSet<String> mTrainTypeFilters = new HashSet<String>();
		
		public void setTrainTypeFilters(String trainTypes){
			mTrainTypeFilters.clear();
			
			if(trainTypes == null || trainTypes.length() == 0){
				return;
			}
			
			String[] types = trainTypes.split("[,;]");
			for(String type : types){
				mTrainTypeFilters.add(type.trim());
			}
			
			Log.i("setTrainTypeFilters ="+mTrainTypeFilters.toString());
		}
				
		public boolean isFiltered(String trainCode){
			if(mTrainTypeFilters.size() == 0){
				return false;
			}
			
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
				return true;
			}			
			return false;
		}
		
		public boolean applayFilter(){
			return mTrainTypeFilters.size() > 0;
		}
	}
	
	private TrainCodeFilter mTrainCodeFilter = new TrainCodeFilter();
	private class TrainCodeFilter{
		private HashMap<String,Integer> mTrainCodeFilters = new LinkedHashMap<String,Integer>();
		private boolean mConsiderOther;
		
		public void setTrainCodeFilters(String trainCodes, String considerOther){
			mConsiderOther = considerOther.equals("true");
			
			mTrainCodeFilters.clear();
			
			if(trainCodes == null || trainCodes.length() == 0){
				return;
			}
			
			String[] codes = trainCodes.split("[,;]");
			int maxWeight = codes.length;
			for(String code : codes){
				mTrainCodeFilters.put(code.trim(),maxWeight);
				maxWeight--;
			}
			
			Log.i("setTrainCodeFilters ="+mTrainCodeFilters.toString());
		}
		
		public int getTrainCodeWeight(String trainCode){
			if(mTrainCodeFilters.containsKey(trainCode)){
				return mTrainCodeFilters.get(trainCode);
			}
			return 0;
		}
		
		public int getMaxWeight(){
			return mTrainCodeFilters.size();
		}
				
		public boolean isFiltered(String trainCode){
			if(!mConsiderOther && (mTrainCodeFilters.size() > 0) && !mTrainCodeFilters.containsKey(trainCode)){
				return true;
			}			
			return false;
		}
		
		public boolean applayFilter(){
			return mTrainCodeFilters.size() > 0;
		}
	}
		
	private SeatFilter mSeatFilter = new SeatFilter();
	private class SeatFilter{
		private HashMap<String,Integer> mSeatFilters = new LinkedHashMap<String,Integer>();
		private HashMap<String,String> mTrainBestSeat = new HashMap<String,String>();
				
		public void setSeatFilters(String seats){
			mTrainBestSeat.clear();
			mSeatFilters.clear();
			
			if(seats == null || seats.length() == 0){
				return;
			}
			
			String[] seatFilters = seats.split("[,;]");
			int maxWeight = seatFilters.length;
			for(String seat : seatFilters){
				mSeatFilters.put(seat.trim(),maxWeight);
				maxWeight--;
			}
			
			Log.i("setSeatFilters ="+mSeatFilters.toString());
		}
		
		public int getSeatWeight(String trainCode){
			if(mTrainBestSeat.containsKey(trainCode)){
				String bestSeat = mTrainBestSeat.get(trainCode);
				return mSeatFilters.get(bestSeat);
			}
			
			return 0;
		}
		
		private int getSeatWeight(JSONObject train){
			String seatNum = null;
			int num = 0;
			int bestWeight = 0;
			for(Map.Entry<String, Integer> entry : mSeatFilters.entrySet()){
				seatNum = train.getString(SeatInfo.getSeatNumKey(entry.getKey()));
				num = SeatInfo.checkSeatNum(seatNum);
				if(/*mSubmitWithoutEnoughTicket || */num >= mPassengerNum){
					bestWeight = entry.getValue();
					String trainCode = train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
					mTrainBestSeat.put(trainCode, entry.getKey());
					break;
				}
			}
			
			return bestWeight;
		}
		
		public int getMaxWeight(){
			return mSeatFilters.size();
		}
				
		public boolean isFiltered(JSONObject train){
			if(getSeatWeight(train) == 0){
				return true;
			}
			return false;
		}
		
		public String getTrainBestSeat(JSONObject train){
			if(train == null){
				return null;
			}
			
			String trainCode = train.getJSONObject("queryLeftNewDTO").getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);
			return mTrainBestSeat.get(trainCode);
		}
	}
	
	/*
	 * return true means filtered
	 */
	private boolean checkFilter(JSONObject train){
		String trainCode = train.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE);		
		return mTrainTypeFilter.isFiltered(trainCode) || mTrainCodeFilter.isFiltered(trainCode)
				|| mSeatFilter.isFiltered(train);
	}
	
	private TrainWeightComparator mWeightComparator = new TrainWeightComparator();
	private class TrainWeightComparator implements Comparator<JSONObject>{
		@Override
	    public int compare(JSONObject train1, JSONObject train2) {
			int weight1 = mTrainCodeFilter.getTrainCodeWeight(train1.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE));
			int weight2 = mTrainCodeFilter.getTrainCodeWeight(train2.getString(TicketInfoConstants.KEY_STATION_TRAIN_CODE));
			if(weight2 > weight1){
				return 1;
			}else if(weight2 < weight1){
				return -1;
			}else{
				return 0;
			}
		}
	}
}
