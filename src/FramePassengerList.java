import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import net.HttpHeader;
import net.HttpResponseHandler;
import net.MyHttpResponse;
import net.MyHttpUrlRequest;
import net.StringHttpResponse;
import net.sf.json.JSONObject;

import util.Log;
import util.TextUtil;
//import InterfaceCommon.onItemCheckedListener;
import util.UrlConstants;


public class FramePassengerList extends JFrame implements HttpResponseHandler{
	private InterfaceCommon.onItemCheckedListener mOnItemCheckedListener;
	private PassengerManager mPassengerManager;
	private JPanel mRemoteListPanel;
	private JPanel mLocalListPanel;
	private JTextField mNameInput;
	private JTextField mIdNoInput;
	private JCheckBox mSexCheckBox;
	private JCheckBox mSaveRemoteCheckBox;
	private MessageDialog mHintDialog;
	
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private Object mLock = new Object();
	
	private LinkedHashMap<String,JCheckBox> mPassengerViewCache = new LinkedHashMap<String,JCheckBox>();
	
	public FramePassengerList(InterfaceCommon.onItemCheckedListener listener){
		mOnItemCheckedListener = listener;
		initFrame();
		initLayout();
		
		mRequestQueue = MainApp.getRequestQueue();
		mPassengerManager = PassengerManager.getInstance();
		mPassengerManager.initPassengers(new PassengerManager.OnPassengersGetDoneListener() {
			@Override
			public void OnRemotePassengersGetDone(HashMap<String,Passenger> passengers) {
				mSaveRemoteCheckBox.setEnabled(true);
				
				addPassengerView(passengers,mRemoteListPanel);				
			}
			@Override
			public void OnLocalPassengersGetDone(HashMap<String,Passenger> passengers) {
				addPassengerView(passengers,mLocalListPanel);
			}
		});
	}
		
	private void addPassengerView(HashMap<String,Passenger> passengers, JPanel panel){
		int count = passengers.size();
		if(passengers == null || count == 0){
			return;
		}

		HashMap<String,Passenger> selectedPassengers = mPassengerManager.getSelectedPassengers();
		for(Map.Entry<String,Passenger> entry : passengers.entrySet()){
			String key = entry.getValue().getIdNo();
			String name = entry.getValue().getName();
			JCheckBox child = new JCheckBox();
			child.setText(name);
			child.setName(key);
			child.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent itemEvent) {
					int state = itemEvent.getStateChange();
					if(mOnItemCheckedListener != null){
						mOnItemCheckedListener.onItemChecked((JCheckBox)itemEvent.getItem(),state == ItemEvent.SELECTED);
					}
				}
			});
			mPassengerViewCache.put(key, child);
			panel.add(child);
			if(selectedPassengers.containsKey(key)){
				child.setSelected(true);
			}
		}
		panel.validate();
		panel.repaint();
	}
	
	public void unSelectPassenger(String key){
		if(mPassengerViewCache.containsKey(key)){
			JCheckBox child = mPassengerViewCache.get(key);
			child.setSelected(false);
		}
	}
	
	private void initFrame(){
		setTitle("乘客");
		setResizable(false);
        setSize(600, 340); 
        setLocationRelativeTo(null); //center in window
        
        mHintDialog = new MessageDialog();
	}

	private void initLayout(){
		LineBorder lineBorder = new LineBorder(Color.BLACK);
		
		JPanel rootPanel = new JPanel();
		this.setContentPane(rootPanel);
		rootPanel.setLayout(new BoxLayout(rootPanel,BoxLayout.Y_AXIS));
		
		JLabel remoteLabel = new JLabel("服务器乘客："); 
		rootPanel.add(remoteLabel);
		mRemoteListPanel = new JPanel();
		mRemoteListPanel.setBorder(lineBorder);
		rootPanel.add(mRemoteListPanel);		
		mRemoteListPanel.setPreferredSize(new Dimension(480,120));
		mRemoteListPanel.setLayout(new GridLayout(3,5));
		
		JLabel localLabel = new JLabel("本地乘客：");
		rootPanel.add(localLabel);
		mLocalListPanel = new JPanel();
		mLocalListPanel.setBorder(lineBorder);
		rootPanel.add(mLocalListPanel);		
		mLocalListPanel.setPreferredSize(new Dimension(480,80));
		mLocalListPanel.setLayout(new GridLayout(3,4));
		
		JPanel addPanel = new JPanel();
		addPanel.setPreferredSize(new Dimension(480,40));
		addPanel.setBorder(lineBorder);
		JLabel nameLabel = new JLabel("姓名：");
		mNameInput = new JTextField();
		mNameInput.setPreferredSize(new Dimension(60,40));
		JLabel idNoLabel = new JLabel("身份证：");
		mIdNoInput = new JTextField();
		mIdNoInput.setPreferredSize(new Dimension(160,40));
		mSexCheckBox = new JCheckBox("男？");
		mSaveRemoteCheckBox = new JCheckBox("保存到服务器");
		mSaveRemoteCheckBox.setEnabled(false);
		
		JButton addBtn = new JButton("添加");
		addBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				addPassenger();
			}
		});
		addPanel.add(nameLabel);
		addPanel.add(mNameInput);
		addPanel.add(idNoLabel);
		addPanel.add(mIdNoInput);
		addPanel.add(mSexCheckBox);
		addPanel.add(mSaveRemoteCheckBox);
		addPanel.add(addBtn);
		rootPanel.add(addPanel);
	}
	
	private void addPassenger(){
		String idNo = mIdNoInput.getText().trim();
		if(mPassengerManager.getPassenger(idNo) != null){
			mHintDialog.showMessage("乘客已添加！");
			return;
		}
		boolean saveRemote = mSaveRemoteCheckBox.isSelected();
		
		Passenger passenger = new Passenger();
		passenger.setName(mNameInput.getText().trim());
		passenger.setIdNo(idNo);
		passenger.setSexCode(mSexCheckBox.isSelected() ? "M" : "F");
		//save passenger data
		if(saveRemote){
			stepAddPassenger(passenger);
			mPassengerManager.addRemotePassenger(passenger);
		}else{
			mPassengerManager.addLocalPassenger(passenger);
		}
		mPassengerManager.addSelectPassenger(passenger);				
		//create passenger view
		JCheckBox child = new JCheckBox();
		child.setText(passenger.getName());
		child.setName(passenger.getIdNo());
		child.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				if(mOnItemCheckedListener != null){
					mOnItemCheckedListener.onItemChecked((JCheckBox)itemEvent.getItem(),state == ItemEvent.SELECTED);
				}
			}
		});
		child.setSelected(true);
		
		if(saveRemote){
			mRemoteListPanel.add(child);
			mRemoteListPanel.validate();
			mRemoteListPanel.repaint();
		}else{
			mLocalListPanel.add(child);
			mLocalListPanel.validate();
			mLocalListPanel.repaint();
		}
		//cache passenger view
		mPassengerViewCache.put(idNo,child);
		
		mNameInput.setText("");
		mIdNoInput.setText("");
		mSexCheckBox.setSelected(false);
		mSaveRemoteCheckBox.setSelected(false);
	}
	
	public void stepAddPassenger(Passenger passenger){
		Log.i("stepAddPassenger");
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("_birthDate", "1984-03-16");
		params.put("address", "");
		params.put("country_code", "CN");
		params.put("email", "");
		params.put("mobile_no", "");
		params.put("passenger_id_no", passenger.getIdNo());
		params.put("passenger_id_type_code", passenger.getIdTypeCode());
		params.put("passenger_name", TextUtil.getUrlEncodeString(passenger.getName()));
		params.put("passenger_type", passenger.getTypeCode());
		params.put("phone_no", "");
		params.put("postalcode", "");
		params.put("sex_code", passenger.getSexCode());
		params.put("studentInfoDTO.department", "");
		params.put("studentInfoDTO.enter_year", "2014");
		params.put("studentInfoDTO.preference_card_no", "");
		params.put("studentInfoDTO.preference_from_station_code", "");
		params.put("studentInfoDTO.preference_from_station_name", "");
		params.put("studentInfoDTO.preference_to_station_code", "");
		params.put("studentInfoDTO.preference_to_station_name", "");
		params.put("studentInfoDTO.province_code", "11");
		params.put("studentInfoDTO.school_class", "");
		params.put("studentInfoDTO.school_code", "");
		params.put("studentInfoDTO.school_name", TextUtil.getUrlEncodeString("简码/汉字"));
		params.put("studentInfoDTO.school_system", "1");
		params.put("studentInfoDTO.student_no", "");
		mRequestQueue.add(new MyHttpUrlRequest("https://kyfw.12306.cn/otn/passengers/add","POST",
				HttpHeader.addPassenger(),params,
				new StringHttpResponse(this,0)));
	}
	
	@Override
	public void handleResponse(MyHttpResponse<?> response){
		synchronized(mLock){		
			Log.i("handleResponse,mStep ="+response.mStep);
			if(response.mResponseCode == 200){
				StringHttpResponse strResponse = (StringHttpResponse)response;
				Log.i(strResponse.mResult);
				JSONObject jObj = JSONObject.fromObject(strResponse.mResult);
				JSONObject jData = jObj.getJSONObject("data");
				String flag = jData.getString("flag");
				if(flag.equals("true")){
					mHintDialog.showMessage("添加成功！");
				}else{
					String message = jData.getString("message");
					mHintDialog.showMessage(message);
				}
			}
		}
	}
}
