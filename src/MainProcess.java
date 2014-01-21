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


public class MainProcess implements UiInterface{
	private FrameLogin mLoginFrame;
	private FrameMain mMainFrame;
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	private PassengerManager mPassengerManager;
	
	private ProcessLogin mProcessLogin;
	private ProcessMainQuery mProcessMainQuery;
	
	public void init(BlockingQueue<MyHttpUrlRequest> queue){
		mUserInfo = new UserInfo();	
		
		mRequestQueue = queue;
		//mRequestProcess.stepGetCookie();	
		
		mProcessLogin = new ProcessLogin(this,mRequestQueue,mUserInfo);
		//FrameMain frame = new FrameMain(mUserInfo,null);
		//frame.setVisible(true);
	}
			
	@Override
	public void loginSuccess(){
		mProcessLogin.setUiVisible(false);		
						
		mProcessMainQuery = new ProcessMainQuery(this,mRequestQueue,mUserInfo,mPassengerManager);
		
		mUserInfo.saveUserInfo();
	}
	
	
	@Override
	public void parseTicketQuery(String str){
		Log.i("parseTicketQuery start");
		Log.i("parseTicketQuery,str="+str);
	}
}
