import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;

import util.Log;

import net.HttpHeader;
import net.HttpResponseHandler;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StringHttpResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ProcessOrderList implements HttpResponseHandler,UiActionListener{
	private FrameOrderCheck mFrameOrderCheck;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private MessageDialog mMessageDialog;
	private String mOrderSequenceNo;
	
	public ProcessOrderList(BlockingQueue<MyHttpUrlRequest> queue){
		mRequestQueue = queue;
		
		mFrameOrderCheck = new FrameOrderCheck(this);
		mMessageDialog = MessageDialog.getInstance();
	}
		
	public void showOrderList(){
		stepInitNoComplete(this);
	}
	
	public void stepInitNoComplete(HttpResponseHandler handler){
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/queryOrder/initNoComplete","GET",
				HttpHeader.getHeader("https://kyfw.12306.cn/otn/queryOrder/initNoComplete"),null,
				new StringHttpResponse(handler,0)));
	}
	
	public void stepQeuryOrderNoComplete(HttpResponseHandler handler){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att","");
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/queryOrder/queryMyOrderNoComplete","POST",
				HttpHeader.getHeader("https://kyfw.12306.cn/otn/queryOrder/initNoComplete"),params,
				new StringHttpResponse(handler,1)));
	}
	
	public void stepCancelNoCompleteMyOrder(HttpResponseHandler handler){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_json_att","");
		params.put("cancel_flag","cancel_order");
		params.put("sequence_no",mOrderSequenceNo);
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/queryOrder/cancelNoCompleteMyOrder","POST",
				HttpHeader.getHeader("https://kyfw.12306.cn/otn/queryOrder/initNoComplete#nogo"),params,
				new StringHttpResponse(handler,2)));
		Log.i("stepCancelNoCompleteMyOrder,mOrderSequenceNo="+mOrderSequenceNo);
	}
	
	@Override
	public void onUiAction(int action){
		if(action == UiActionListener.UI_ACTION_ORDER_CANCEL){
			stepCancelNoCompleteMyOrder(this);
		}
	}
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		if(response.mStep == 0){
			//Log.i(((StringHttpResponse)response).mResult);
			if(response.mResponseCode == 200){
				stepQeuryOrderNoComplete(this);
			}
		}else if(response.mStep == 1){
			Log.i(((StringHttpResponse)response).mResult);
			parseQeuryOrderNoComplete(((StringHttpResponse)response).mResult);
		}else if(response.mStep == 2){
			parseCancelNoCompleteMyOrder(((StringHttpResponse)response).mResult);
		}
	}
	
	private void parseCancelNoCompleteMyOrder(String response){
		JSONObject jObj = JSONObject.fromObject(response);
		String message = jObj.getString("messages");
		if(message.equals("[]")){			
			if(jObj.containsKey("data")){
				JSONObject jData = jObj.getJSONObject("data");
				String existError = jData.getString("existError");
				if(existError.equals("N")){
					mMessageDialog.showMessage("取消成功");
				}else{
					mMessageDialog.showMessage("取消失败");
				}
			}else{
				
			}
		}else{
			mMessageDialog.showMessage(message);
		}
	}
		
	private void parseQeuryOrderNoComplete(String response){
		JSONObject jObj = JSONObject.fromObject(response);
		String message = jObj.getString("messages");
		if(message.equals("[]")){
			if(jObj.containsKey("data")){
				JSONObject jData = jObj.getJSONObject("data");
				String orderDBList = jData.getString("orderDBList");
				//Log.i("orderDBList str = "+orderDBList);
				JSONArray orderDBArray = JSONArray.fromObject(orderDBList);
				JSONObject orderDB = orderDBArray.getJSONObject(0);
				mOrderSequenceNo = orderDB.getString("sequence_no");
				String ticktes = orderDB.getString("tickets");
				//Log.i("tickets str = "+ticktes);
				JSONArray orderList = JSONArray.fromObject(ticktes);
				//Log.i("oderList size="+orderList.size());
				
				mFrameOrderCheck.setVisible(true);
				mFrameOrderCheck.updateOrderList(orderList);
			}else{
				mMessageDialog.showMessage("您没有未完成订单，可以通过车票预订 功能，来制定出行计划。");
			}
		}else{
			mMessageDialog.showMessage(message);
		}
		/*
		String status = jObj.getString("status");
		if(status.equals("true")){
			//try{
				JSONObject data = jObj.getJSONObject("data");
				String orderDBList = data.getString("orderDBList");
				Log.i("orderDBList str = "+orderDBList);
				JSONArray orderArray = JSONArray.fromObject(orderDBList);
				String ticktes = orderArray.getJSONObject(0).getString("tickets");
				Log.i("tickets str = "+ticktes);
				JSONArray orderList = JSONArray.fromObject(ticktes);
				//String tickets = data.getString("tickets");		
				//JSONArray orderList = JSONArray.fromObject(tickets);
				Log.i("oderList size="+orderList.size());
				//mFrameOrderCheck.updateOrderList(tickets);
			//}catch(net.sf.json.JSONException e){
				//mMessageDialog.showMessage("您没有未完成订单，可以通过车票预订 功能，来制定出行计划。");
			//}
		}	*/	
	}
}
