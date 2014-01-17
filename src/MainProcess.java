import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import util.Log;
import util.TicketInfoConstants;

import net.HttpDispatcher;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class MainProcess implements UiInterface, UiActionListener{
	private FrameLogin mLoginFrame;
	private FrameMain mMainFrame;
	private RequestProcess mRequestProcess;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	private PassengerManager mPassengerManager;
	
	public void init(BlockingQueue<MyHttpUrlRequest> queue){
		mUserInfo = new UserInfo();						
		mLoginFrame = new FrameLogin(this); 
		mLoginFrame.setVisible(true); 
		mLoginFrame.setUserInfo(mUserInfo);
		
		mRequestQueue = queue;
		mRequestProcess = new RequestProcess(mRequestQueue,this,mUserInfo);
		mRequestProcess.stepGetCookie();	
	}
	
	@Override
	public void onUiAction(int action){
		switch(action){
			case UiActionListener.UI_ACTION_TICKET_AUTO_QUERY:
				mRequestProcess.stepQueryLeft();
				break;
			case UiActionListener.UI_ACTION_USER_LOGIN:
				mRequestProcess.stepLoginAyncSuggest();
				break;
		}
	}
	
	@Override
	public void setLoginCaptcha(ImageIcon icon){
		mLoginFrame.setCaptchaIcon(icon);
	}
		
	@Override
	public void loginSuccess(){
		mLoginFrame.setVisible(false); 
		if(mMainFrame == null){
			mMainFrame = new FrameMain(mUserInfo,this);
			mMainFrame.setTickProcess(mRequestProcess);
		}
		mMainFrame.setVisible(true);
		
		mPassengerManager = new PassengerManager(mRequestProcess);
		mPassengerManager.initPassengers();
		
		mUserInfo.saveUserInfo();
	}
	
	
	@Override
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
		}
		Log.i("===================================================================");
		Log.i("parseTicketQuery,bestTrain="+train);
		Log.i("parseTicketQuery end");
	}
	
	private ProcessSubmitOrder mProcessSubmitOrder;
	private void submitOrder(TicketInfo tickInfo){
		mPassengerNum = mPassengerManager.getSelectedPassengers().size();
		mProcessSubmitOrder = new ProcessSubmitOrder(this,mRequestQueue,mUserInfo,mPassengerManager);
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
			seatNum = train.getString(seatFilter.get(i));
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
		if(seatNum.equals("нч") || seatNum.equals("--")){
			//return num;
		}else if(seatNum.equals("сп")){
			num = ENOUGH_SEAT_NUM;
		}else{
			num = Integer.valueOf(seatNum);
		}
		return num;
	}
}
