import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.CookieManager;
import net.sf.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MaskFormatter;

import util.DateHelper;
import util.Log;
import util.TextUtil;


public class FrameMain extends JFrame{
	private UserInfo mUserInfo;
	private UiActionListener mUiActionListener;
	
	private JTextField mFromStationInput;
	private JTextField mToStationInput;
	private JFormattedTextField mDateInput;
	private JTextField mTrainFilterInput;
	private JButton mQueryBtn;
	private JPanel mSeatPanel;
	private JPanel mRootPanel;
	private JPanel mPassengerPanel;
	private JTextArea mLogLabel;
	private JCheckBox mAutoCheckBox;
	private JCheckBox mPrioritySeatCheckBox;
	private JCheckBox mPriorityTrainCheckBox;
	private JCheckBox mQueryModeQiangCheckBox;
	private JCheckBox mQueryModeJianCheckBox;
	private JCheckBox mSubmitWithoutEnoughCheckBox;
	
	private JCheckBox mConsiderOtherTrains;
	private TrainListTableModel mTrainListModel;
	
	private boolean mAutoQueryStart;
	
	private SeatInfo mSeatInfo;
	private PassengerManager mPassengerManager;
	
	private HashSet<String> mTrainTypeFilters = new LinkedHashSet<String>();
	
	private MessageDialog mMessageDialog = new MessageDialog();
	
	public FrameMain(UserInfo userInfo, UiActionListener listener){
		mUserInfo = userInfo;
		mUiActionListener = listener;
		mPassengerManager = PassengerManager.getInstance();
		initFrame();
	}
	
	private void initFrame(){
		setTitle("主窗口");
		setResizable(true);
        setSize(800, 560); 
        setLocationRelativeTo(null); //center in window
        addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) { 
                //System.out.println("Exit when Closed event"); 
                System.exit(0);
            }
        });
        
        initTicketInfoLayout();
	}
	GridBagLayout gridBagLayout = new GridBagLayout();
	private void initTicketInfoLayout(){
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill =GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
				
		mRootPanel = new JPanel();
		mRootPanel.setLayout(gridBagLayout);
		this.setContentPane(mRootPanel);
		
		initStationDatePanel(mRootPanel,c);
		initTrainTypeFilterPanel(mRootPanel,c);
		initTrainFilterPanel(mRootPanel,c);
		initPassengerPanel(mRootPanel,c);
		initSeatPanel(mRootPanel,c);
		initQueryOptionPanel(mRootPanel,c);	
		initTrainListTablePanel(mRootPanel,c);
		initMessagePanel(mRootPanel,c);
	}
		
	private void initStationDatePanel(JPanel parent, GridBagConstraints c){
		c.gridy = 0;
		c.gridheight = 1;
		c.insets = new Insets(8,0,0,0);
		//from station
		JLabel fromStation = new JLabel();
		fromStation.setText("出发地：");
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(fromStation, c);
		
		mFromStationInput = new JTextField();
		c.gridx = 1;		
		c.weightx = 1;
		c.gridwidth = 2;
		mFromStationInput.setText(mUserInfo.getFromStationName());
		parent.add(mFromStationInput,c);
		// to station
		JLabel toStation = new JLabel();
		toStation.setText("目的地：");
		c.gridx = 3;
		c.weightx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		parent.add(toStation,c);
		mToStationInput = new JTextField();
		c.gridx = 4;
		c.weightx = 1;
		c.gridwidth = 2;
		mToStationInput.setText(mUserInfo.getToStationName());
		parent.add(mToStationInput,c);
		//date
		JLabel date = new JLabel();
		date.setText("出发日期：");
		c.gridx = 6;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(date,c);
		
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("####-##-##");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		mDateInput = new JFormattedTextField(mf);
		c.gridx = 7;
		c.weightx = 1;
		c.gridwidth = 2;
		mDateInput.setColumns(10);
		if (!TextUtil.isEmpty(mUserInfo.getDate())) {
			mDateInput.setText(mUserInfo.getDate());
		}
		parent.add(mDateInput,c);
	}
	
	private void initTrainTypeFilterPanel(JPanel parent, GridBagConstraints c){
		c.gridy = 2;
		c.gridheight = 1;
		
		JLabel trainTypeTag = new JLabel("车次类型：");
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		gridBagLayout.setConstraints(trainTypeTag,c);
		parent.add(trainTypeTag);
		
		JPanel trainTypePanel = new JPanel();
		trainTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		c.gridx = 1;
		c.weightx = 1;
		c.gridwidth = 8;
		parent.add(trainTypePanel,c);
		
		String trainTypeFilter = mUserInfo.getTrainTypeFilter();
		if(trainTypeFilter != null){
			String[] filters = trainTypeFilter.split("[,]");
			for(int i=0;i<filters.length;i++){
				mTrainTypeFilters.add(filters[i]);
			}
		}
	
		for(int i=0;i<mTrainTypeKeys.length;i++){
			JCheckBox child = new JCheckBox(mTrainTypeNames[i]);
			child.setName(mTrainTypeKeys[i]);
			trainTypePanel.add(child);
			if(mTrainTypeFilters.contains(mTrainTypeKeys[i])){
				child.setSelected(true);
			}
			child.addItemListener(mTrainTypeItemCheckListener);
			mTrainTypeViewCache.put(mTrainTypeKeys[i],child);
		}
	}
	
	private void initTrainFilterPanel(JPanel parent, GridBagConstraints c){
		c.gridy = 1;
		c.gridheight = 1;
		
		JLabel trainFilter = new JLabel();
		trainFilter.setText("优先车次：");
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(trainFilter,c);
		mTrainFilterInput = new JTextField();
		c.gridx = 1;
		c.weightx = 1;
		c.gridwidth = 6;
		mTrainFilterInput.setText(mUserInfo.getTrainFilter());
		parent.add(mTrainFilterInput,c);
		mConsiderOtherTrains = new JCheckBox("考虑其他车次？");
		mConsiderOtherTrains.setSelected(mUserInfo.getConsiderOtherTrain().equals("true"));
		c.gridx = 8;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(mConsiderOtherTrains,c);
	}
	
	private void initQueryOptionPanel(JPanel parent, GridBagConstraints c){
		c.gridy = 7;
		c.gridheight = 1;
		JLabel optionLabel = new JLabel("选项：");
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(optionLabel,c);
		
		mQueryBtn = new JButton("查询");
		mQueryBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				mLogLabel.setText("Message:");
				if(mAutoQueryStart || checkUserInfo()){
					mUiActionListener.onUiAction(UiActionListener.UI_ACTION_TICKET_QUERY);
				}
			}
		});
		c.gridx = 1;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(mQueryBtn,c);
		
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		c.gridx = 2;		
		c.weightx = 1;
		c.gridwidth = 8;
		parent.add(optionPanel,c);
		
		mAutoCheckBox = new JCheckBox("自动");
		mAutoCheckBox.setSelected(mUserInfo.getQueryAuto().equals("true"));
		optionPanel.add(mAutoCheckBox);		
		
		LineBorder lineBorder = new LineBorder(Color.GRAY);
		JPanel priorityPanel = new JPanel();
		priorityPanel.setBorder(lineBorder);
		optionPanel.add(priorityPanel);
		String prioirtyType = mUserInfo.getPriorityType();
		mPrioritySeatCheckBox = new JCheckBox("席别优先");
		mPrioritySeatCheckBox.setName(UserInfo.PRIORITY_TYPE_SEAT);
		if(prioirtyType.equals(UserInfo.PRIORITY_TYPE_SEAT)){
			mPrioritySeatCheckBox.setSelected(true);
		}
		mPrioritySeatCheckBox.addItemListener(mPriorityItemListener);		
		priorityPanel.add(mPrioritySeatCheckBox);
		
		mPriorityTrainCheckBox = new JCheckBox("车次优先");
		mPriorityTrainCheckBox.setName(UserInfo.PRIORITY_TYPE_TRAIN);
		if(prioirtyType.equals(UserInfo.PRIORITY_TYPE_TRAIN)){
			mPriorityTrainCheckBox.setSelected(true);
		}
		mPriorityTrainCheckBox.addItemListener(mPriorityItemListener);
		priorityPanel.add(mPriorityTrainCheckBox);
		
		JPanel modePanel = new JPanel();
		modePanel.setBorder(lineBorder);
		optionPanel.add(modePanel);
		String queryMode = mUserInfo.getQueryMode();
		mQueryModeQiangCheckBox = new JCheckBox("抢票");
		mQueryModeQiangCheckBox.setName(UserInfo.QUERY_MODE_QIANG);
		if(queryMode.equals(UserInfo.QUERY_MODE_QIANG)){
			mQueryModeQiangCheckBox.setSelected(true);
		}
		mQueryModeQiangCheckBox.addItemListener(mQueryModeItemListener);
		modePanel.add(mQueryModeQiangCheckBox);
		
		mQueryModeJianCheckBox = new JCheckBox("捡漏");
		mQueryModeJianCheckBox.setName(UserInfo.QUERY_MODE_JIAN);
		if(queryMode.equals(UserInfo.QUERY_MODE_JIAN)){
			mQueryModeJianCheckBox.setSelected(true);
		}
		mQueryModeJianCheckBox.addItemListener(mQueryModeItemListener);
		modePanel.add(mQueryModeJianCheckBox);
		
		mSubmitWithoutEnoughCheckBox = new JCheckBox("余票不足部分提交");
		mSubmitWithoutEnoughCheckBox.setEnabled(false);
		mSubmitWithoutEnoughCheckBox.setName(UserInfo.QUERY_MODE_JIAN);
		if(mUserInfo.getSubmitWithoutEnoughTicket().equals("true")){
			mSubmitWithoutEnoughCheckBox.setSelected(true);
		}
		optionPanel.add(mSubmitWithoutEnoughCheckBox);
	}
	
	private void initTrainListTablePanel(JPanel parent, GridBagConstraints c) {
		mTrainListModel = new TrainListTableModel();
		JTable trainList = new JTable(mTrainListModel);
		trainList.setRowHeight(30);
		trainList.setPreferredScrollableViewportSize(new Dimension(500, 200));
		trainList.setFillsViewportHeight(true);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		trainList.setDefaultRenderer(Object.class, tcr);
		c.gridx = 0;
		c.gridy = 8;
		c.weighty = 1;
		c.gridwidth = 9;
		JScrollPane scrollPane = new JScrollPane(trainList);
		parent.add(scrollPane, c);
	}

	private void initMessagePanel(JPanel parent, GridBagConstraints c) {
		mLogLabel = new JTextArea("Message:");
		mLogLabel.setBackground(new Color(0, 0, 0, 0));
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 9;
		c.gridheight = 1;
		mRootPanel.add(mLogLabel, c);
	}

	private ItemListener mPriorityItemListener = new ItemListener(){
		public void itemStateChanged(ItemEvent itemEvent) {
			JCheckBox child = (JCheckBox)itemEvent.getItem();
			int state = itemEvent.getStateChange();
			if(state == ItemEvent.SELECTED){
				if(child == mPrioritySeatCheckBox){
					mPriorityTrainCheckBox.setSelected(false);
				}else{
					mPrioritySeatCheckBox.setSelected(false);
				}
			}
		}
	};
	private ItemListener mQueryModeItemListener = new ItemListener(){
		public void itemStateChanged(ItemEvent itemEvent) {
			JCheckBox child = (JCheckBox)itemEvent.getItem();
			int state = itemEvent.getStateChange();
			if(state != ItemEvent.SELECTED){
				if(child == mQueryModeQiangCheckBox){
					mQueryModeJianCheckBox.setSelected(false);
				}else{
					mQueryModeQiangCheckBox.setSelected(false);
				}
			}
		}
	};
	private void initPassengerPanel(JPanel parent, GridBagConstraints c){
		c.gridy = 3;
		c.gridheight = 1;
		
		JLabel label = new JLabel("乘客：");
		c.gridx = 0;		
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(label,c);
		
		JButton addSeat = new JButton("添加");
		addSeat.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				showPassengerList();
			}
		});
		c.gridx = 1;		
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(addSeat,c);
		
		mPassengerPanel = new JPanel();
		mPassengerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//mPassengerPanel.setPreferredSize(new Dimension(300,60));//cann't change line without this line
		c.gridx = 2;		
		c.weightx = 1;
		c.gridwidth = 7;
		parent.add(mPassengerPanel,c);
		
		mPassengerManager.selectMultiPassengers(mUserInfo.getPassengers());
		HashMap<String,Passenger> passengers = mPassengerManager.getSelectedPassengers();
		for(Map.Entry<String, Passenger> entry : passengers.entrySet()){
			Passenger passenger = entry.getValue();
			JCheckBox child = new JCheckBox();
			child.setName(passenger.getIdNo());
			child.setText(passenger.getName());
			child.setSelected(true);
			child.addItemListener(mPassengerItemListener);
			mPassengerJCheckBoxCache.put(passenger.getIdNo(), child);
			mPassengerPanel.add(child);
		}
	}
		
	private void initSeatPanel(JPanel parent, GridBagConstraints c){
		c.gridy = 5;
		c.gridheight = 1;
		
		mSeatInfo = new SeatInfo();
		mSeatInfo.selectMultiSeatType(mUserInfo.getSeatFitlerArray());
		
		JLabel label = new JLabel("优先席别：");
		c.gridx = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(label,c);
		
		JButton addSeat = new JButton("添加");
		addSeat.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				showSeatList();
			}
		});
		c.gridx = 1;
		c.weightx = 0;
		c.gridwidth = 1;
		parent.add(addSeat,c);
		
		mSeatPanel = new JPanel();		
		mSeatPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//mPassengerPanel.setPreferredSize(new Dimension(300,60));//cann't change line without this line
		c.gridx = 2;
		c.weightx = 1;
		c.gridwidth = 7;
		parent.add(mSeatPanel,c);
		
		JCheckBox[] jCheckBoxs = mSeatInfo.getSelectedSeatTypeCheckBoxs();
		if(jCheckBoxs != null){
			for(JCheckBox checkBox : jCheckBoxs){
				checkBox.setSelected(true);
				checkBox.addItemListener(mSeatItemListener);
				mSeatPanel.add(checkBox);
				Log.i("initSeatPanel,key="+checkBox.getName());
				mSeatJCheckBoxCache.put(checkBox.getName(), checkBox);
			}
		}
	}
	
	private ItemListener mSeatItemListener = new ItemListener(){
		public void itemStateChanged(ItemEvent itemEvent) {
			JCheckBox child = (JCheckBox)itemEvent.getItem();
			int state = itemEvent.getStateChange();
			if(state != ItemEvent.SELECTED){
				String key = child.getName();
				mSeatInfo.unSelectSingleSeatType(key);
				if(mFrameSeatList != null){
					mFrameSeatList.unselectSeatType(key);
				}
				mSeatPanel.remove(child);
				mSeatPanel.validate();
				mSeatPanel.repaint();
			}
		}
	};
	
	private FrameSeatList mFrameSeatList = null;
	private LinkedHashMap<String,JCheckBox> mSeatJCheckBoxCache = new LinkedHashMap<String,JCheckBox>(10);
	private void showSeatList(){
		if(mFrameSeatList == null){
			mFrameSeatList = new FrameSeatList(new FrameSeatList.onItemCheckedListener() {
				@Override
				public void onItemChecked(JCheckBox item, boolean checked) {
					Log.i("onItemChecked,checked="+checked);
					String key = item.getName();
					JCheckBox child = null;
					Log.i("showSeatList,key="+key);
					if(mSeatJCheckBoxCache.containsKey(key)){
						child = mSeatJCheckBoxCache.get(key);
					}
					if(checked){
						if(child == null){
							Log.i("showSeatList,new child");
							child = new JCheckBox();
							child.setName(key);
							child.setText(item.getText());							
							child.addItemListener(mSeatItemListener);
							mSeatJCheckBoxCache.put(key, child);
						}
						child.setSelected(true);
						
						mSeatInfo.selectSingleSeatType(key);
						mSeatPanel.add(child);
					}else{
						mSeatInfo.unSelectSingleSeatType(key);
						mSeatPanel.remove(child);
					}
					mSeatPanel.validate();
					mSeatPanel.repaint();
					mRootPanel.validate();
					mRootPanel.repaint();
				}
			});
			mFrameSeatList.initSelectedSeats(mUserInfo.getSeatFitlerArray());
		}
		mFrameSeatList.setVisible(true);
	}
	
	private ItemListener mPassengerItemListener = new ItemListener(){
		public void itemStateChanged(ItemEvent itemEvent) {
			JCheckBox child = (JCheckBox)itemEvent.getItem();
			int state = itemEvent.getStateChange();
			if(state != ItemEvent.SELECTED){
				String key = child.getName();
				mPassengerManager.unSelectSinglePassenger(key);
				if(mFramePassengerList != null){
					mFramePassengerList.unSelectPassenger(key);
				}
				mPassengerPanel.remove(child);
				mPassengerPanel.validate();
				mPassengerPanel.repaint();
				mRootPanel.validate();
				mRootPanel.repaint();
			}
		}
	};
	
	private FramePassengerList mFramePassengerList = null;
	private LinkedHashMap<String,JCheckBox> mPassengerJCheckBoxCache = new LinkedHashMap<String,JCheckBox>(10);
	private void showPassengerList(){
		if(mFramePassengerList == null){
			mFramePassengerList = new FramePassengerList(new InterfaceCommon.onItemCheckedListener() {
				@Override
				public void onItemChecked(JCheckBox item, boolean checked) {
					Log.i("onItemChecked,checked="+checked);
					String key = item.getName();
					JCheckBox child = null;
					if(mPassengerJCheckBoxCache.containsKey(key)){
						child = mPassengerJCheckBoxCache.get(key);
					}
					if(checked){
						if(child == null){
							child = new JCheckBox();
							child.setName(key);
							child.setText(item.getText());							
							child.addItemListener(mPassengerItemListener);
							mPassengerJCheckBoxCache.put(key, child);
						}
						child.setSelected(true);
						mPassengerManager.selectSinglePassenger(key);
						mPassengerPanel.add(child);
					}else{
						mPassengerManager.unSelectSinglePassenger(key);
						mPassengerPanel.remove(child);
					}
					mPassengerPanel.validate();
					mPassengerPanel.repaint();
				}
			});
		}
		mFramePassengerList.setVisible(true);
	}
	
	private static final String[] mTrainTypeKeys = {
		//UserInfo.TRAIN_TYPE_FILTER_ALL,
		UserInfo.TRAIN_TYPE_FILTER_G,
		UserInfo.TRAIN_TYPE_FILTER_D,
		UserInfo.TRAIN_TYPE_FILTER_Z,
		UserInfo.TRAIN_TYPE_FILTER_T,
		UserInfo.TRAIN_TYPE_FILTER_K,
		UserInfo.TRAIN_TYPE_FILTER_Q
	};
	private static final String[] mTrainTypeNames = {/*"全部",*/"G-高铁","D-动车","Z-直达","T-特快","K-快车","Q-其他"};
	private static final int mTrainTypeMax = mTrainTypeNames.length-1;
	private HashMap<String,JCheckBox> mTrainTypeViewCache = new HashMap<String,JCheckBox>(mTrainTypeKeys.length);
	private ItemListener mTrainTypeItemCheckListener = new ItemListener(){
		public void itemStateChanged(ItemEvent itemEvent) {
			JCheckBox child = (JCheckBox)itemEvent.getItem();
			int state = itemEvent.getStateChange();
			String key = child.getName();
			if(state == ItemEvent.SELECTED){				
				if(key.equals(UserInfo.TRAIN_TYPE_FILTER_ALL)){
					trainTypeCheckAll(true);
				}else{
					mTrainTypeFilters.add(key);
				}
			}else{
				if(key.equals(UserInfo.TRAIN_TYPE_FILTER_ALL)){
					trainTypeCheckAll(false);
				}else{
					mTrainTypeFilters.remove(key);
				}
			}
			JCheckBox allChild = mTrainTypeViewCache.get(UserInfo.TRAIN_TYPE_FILTER_ALL);
			if(allChild != null){
				if(mTrainTypeFilters.size() == mTrainTypeMax){
					allChild.setSelected(true);
				}else{
					allChild.setSelected(false);
				}
			}
		}
	};
	
	private void trainTypeCheckAll(boolean check){
		for(Map.Entry<String, JCheckBox> entry : mTrainTypeViewCache.entrySet()){
			JCheckBox child = entry.getValue();
			String key = child.getName();
			if(!key.equals(UserInfo.TRAIN_TYPE_FILTER_ALL)){
				child.setSelected(check);
				if(check){
					mTrainTypeFilters.add(key);
				}else{
					mTrainTypeFilters.remove(key);
				}
			}
		}
	}
	
	public void setQueryState(boolean queryStart){
		mAutoQueryStart = queryStart;
		if (queryStart) {
			mQueryBtn.setText("停止");
		} else {
			mQueryBtn.setText("查询");
		}
	}
	
	private boolean checkUserInfo(){
		if(!checkTicketInfo()){
			return false;
		}
		if(!checkDate()){
			return false;
		}
		//if(mAutoCheckBox.isSelected()){			
			if(!checkFilterInfo()){
				return false;
			}			
		//}
		checkTrainTypeFilter();
		checkOptions();		
		
		mUserInfo.saveUserInfo();
		resetCookie();
		return true;
	}
	
	private void resetCookie(){
		Map<String, String> cookies = new LinkedHashMap<String, String>();
		cookies.put("_jc_save_fromStation", TicketHelper.getUnicode(mUserInfo.getFromStationName(), mUserInfo.getFromStationCode()));
		cookies.put("_jc_save_toStation", TicketHelper.getUnicode(mUserInfo.getToStationName(), mUserInfo.getToStationCode()));
		cookies.put("_jc_save_fromDate",mUserInfo.getDate());
		cookies.put("_jc_save_toDate",DateHelper.getCurDate());
		cookies.put("_jc_save_wfdc_flag","dc");
		CookieManager cookieManager = MainApp.getCookieManager();
		cookieManager.add(cookies);
	}
	
	private boolean checkTicketInfo(){		
		//check from station
		String fromStation = mFromStationInput.getText();
		if(TextUtil.isEmpty(fromStation)){
			mMessageDialog.showMessage("请填写出发地！");
			return false;
		}
		String fromStationCode = TicketHelper.getCityCode(fromStation);
		if(TextUtil.isEmpty(fromStationCode)){
			mMessageDialog.showMessage("出发地错误！");
			return false;
		}
		//check to station
		String toStation = mToStationInput.getText(); 
		if(TextUtil.isEmpty(toStation)){
			mMessageDialog.showMessage("请填写目的地！");
			return false;
		}
		String toStationCode = TicketHelper.getCityCode(toStation);
		if(TextUtil.isEmpty(toStationCode)){
			mMessageDialog.showMessage("目的地错误！");
			return false;
		}
		
		mUserInfo.setFromStationName(fromStation);
		mUserInfo.setFromStationCode(fromStationCode);
		mUserInfo.setToStationName(toStation);
		mUserInfo.setToStationCode(toStationCode);		
		mUserInfo.setPassengers(mPassengerManager.getSelectedPassengersString());
		
		return true;
	}
	
	private boolean checkFilterInfo(){
		//String[] seatTypes = mSeatInfo.getSelectedSeatTypes();
		//if(seatTypes != null && seatTypes.length > 0){
			mUserInfo.setSeatFilterByArray(mSeatInfo.getSelectedSeatTypes());
		//}
		String trainFilter = mTrainFilterInput.getText();
		//if(!TextUtil.isEmpty(trainFilter)){
			mUserInfo.setTrainFilter(trainFilter);
		//}
			Log.i("checkFilterInfo,trainFilter="+trainFilter);
			
		String considerOther = mConsiderOtherTrains.isSelected() ? "true" : "false";				
		mUserInfo.setConsiderOtherTrain(considerOther);
		return true;
	}
	
	private boolean checkDate(){
		boolean validDate = true;
		String date = mDateInput.getText();
		String[] strs = date.split("[-]");
		try{
			int year = Integer.valueOf(strs[0]);
			int month = Integer.valueOf(strs[1]);
			int day = Integer.valueOf(strs[2]);
			Log.i("checkDate,year="+year+",month="+month+",day="+day);
			Calendar cal=Calendar.getInstance();
			int curr_year = cal.get(Calendar.YEAR);
			if(year < curr_year){
				validDate = false;
			}else if(month <= 0 || month > 12){
				validDate = false;
			}else if(day <= 0 || day > 31){
				validDate = false;
			}
		}catch(NumberFormatException e){
			validDate = false;
		}
		
		if(!validDate){
			mMessageDialog.showMessage("日期错误！");
		}else{
			mUserInfo.setDate(date);
		}
		
		return validDate;
	}
	
	private void checkTrainTypeFilter(){
		mTrainTypeFilters.clear();
		
		String filter = "";
		for(Map.Entry<String, JCheckBox> entry : mTrainTypeViewCache.entrySet()){
			JCheckBox child = entry.getValue();
			String key = child.getName();
			if(key.equals(UserInfo.TRAIN_TYPE_FILTER_ALL)){
				continue;
			}
			if(child.isSelected()){
				if(filter.length() > 0){
					filter += ",";
				}
				filter += key;
				mTrainTypeFilters.add(key);
			}
		}
		mUserInfo.setTrainTypeFilter(filter);
	}
	
	private void checkOptions(){
		String queryAuto = mAutoCheckBox.isSelected() ? "true" : "false";
		mUserInfo.setQueryAuto(queryAuto);
		String queryMode = mQueryModeQiangCheckBox.isSelected() ? UserInfo.QUERY_MODE_QIANG : UserInfo.QUERY_MODE_JIAN;
		mUserInfo.setQueryMode(queryMode);
		String priority = "UserInfo.PRIORITY_TYPE_NONE";
		if(mPrioritySeatCheckBox.isSelected()){
			priority = UserInfo.PRIORITY_TYPE_SEAT;
		}else{
			priority = UserInfo.PRIORITY_TYPE_TRAIN;
		}
		mUserInfo.setPriorityType(priority);
		mUserInfo.setSubmitWithoutEnoughTicket(mSubmitWithoutEnoughCheckBox.isSelected() ? "true" : "false");
	}
		
	public void showLog(String... messages){
		String msg = new String();
		for (String message : messages) {
			msg += message+"\n";
		}
		mLogLabel.setText(msg);
	}
	
	public HashSet<String> getTrainTypeFilter(){
		return mTrainTypeFilters;
	}
	
	public void updateTrainListTable(ArrayList<JSONObject> trainList){
		mTrainListModel.updateData(trainList);
	}
}
