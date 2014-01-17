import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.CookieManager;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import util.DateHelper;
import util.Log;
import util.TextUtil;


public class FrameMain extends JFrame{
	private UserInfo mUserInfo;
	private RequestProcess mTicketProcess;
	private UiActionListener mUiActionListener;
	
	private JTextField mFromStationInput;
	private JTextField mToStationInput;
	private JFormattedTextField mDateInput;
	private JTextField mTrainFilterInput;
	private JTextField mSeatFilterInput;
	
	public FrameMain(UserInfo userInfo, UiActionListener listener){
		mUserInfo = userInfo;
		mUiActionListener = listener;
		initFrame();
	}
	
	private void initFrame(){
		setTitle("主窗口");
		setResizable(true);
        setSize(600, 600); 
        setLocationRelativeTo(null); //center in window
        addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) { 
                //System.out.println("Exit when Closed event"); 
                System.exit(0);
            }
        });
        
        initTicketInfoLayout();
	}
	
	private void initTicketInfoLayout(){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		this.add(panel);
		
		final int ROW_HEIGHT = 42;
		final int TOP_PADDING = 20;
		final int LEFT_PADDING = 20;
		final int ROW_GAP = 15;
		final int COL_GAP = 10;
		final int LABEL_WIDTH = 60;
		final int INPUT_WIDTH = 200;
		final int CAPTCHA_WIDTH = 78;
		final int STATION_INPUT_WIDTH = 100;
		final int DATE_INPUT_WIDTH = 150;
		final int FILTER_INPUT_WIDTH = 200;
		
		int xOffset = LEFT_PADDING; //left padding
		int yOffset = TOP_PADDING; //top padding
		
		//from station
		JLabel fromStation = new JLabel();
		fromStation.setText("出发地:");
		fromStation.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);
		panel.add(fromStation);		
		xOffset += LABEL_WIDTH;		
		mFromStationInput = new JTextField();
		mFromStationInput.setBounds(xOffset,yOffset,STATION_INPUT_WIDTH,ROW_HEIGHT);		
		mFromStationInput.setText(mUserInfo.getFromStationName());
		panel.add(mFromStationInput);
		//to station
		xOffset += STATION_INPUT_WIDTH+20;	
		JLabel toStation = new JLabel();
		toStation.setText("目的地:");
		toStation.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);
		panel.add(toStation);		
		xOffset += LABEL_WIDTH;		
		mToStationInput = new JTextField();
		mToStationInput.setBounds(xOffset,yOffset,STATION_INPUT_WIDTH,ROW_HEIGHT);
		mToStationInput.setText(mUserInfo.getToStationName());
		panel.add(mToStationInput);
		//date
		xOffset += STATION_INPUT_WIDTH+20;	
		JLabel date = new JLabel();
		date.setText("出发日期:");
		date.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);		
		panel.add(date);		
		xOffset += LABEL_WIDTH;	
		
		MaskFormatter mf = null;
		try {
			mf = new MaskFormatter("####-##-##");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		mDateInput = new JFormattedTextField(mf);
		mDateInput.setBounds(xOffset,yOffset,DATE_INPUT_WIDTH,ROW_HEIGHT);
		mDateInput.setColumns(10);
		if(!TextUtil.isEmpty(mUserInfo.getDate())){
			mDateInput.setText(mUserInfo.getDate());
		}
		panel.add(mDateInput);	
		
		//train filter
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_HEIGHT+10;
		JLabel trainFilter = new JLabel();
		trainFilter.setText("车次");
		trainFilter.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);
		panel.add(trainFilter);
		xOffset += LABEL_WIDTH;		
		mTrainFilterInput = new JTextField();
		mTrainFilterInput.setBounds(xOffset,yOffset,FILTER_INPUT_WIDTH,ROW_HEIGHT);		
		mTrainFilterInput.setText(mUserInfo.getTrainFitler().toString());
		panel.add(mTrainFilterInput);
		
		//seat filter
		xOffset = LEFT_PADDING; // left padding
		yOffset += ROW_HEIGHT + 10;
		JLabel seatFilter = new JLabel();
		seatFilter.setText("席别");
		seatFilter.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);
		panel.add(seatFilter);
		xOffset += LABEL_WIDTH;
		mSeatFilterInput = new JTextField();
		mSeatFilterInput.setBounds(xOffset, yOffset, FILTER_INPUT_WIDTH,ROW_HEIGHT);
		mSeatFilterInput.setText(mUserInfo.getSeatFitler().toString());
		panel.add(mSeatFilterInput);
		
		//qury button
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_HEIGHT+10;
		JButton qureyBtn = new JButton();
		qureyBtn.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);
		qureyBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				if(checkUserInfo()){
					mUiActionListener.onUiAction(UiActionListener.UI_ACTION_TICKET_AUTO_QUERY);
				}
			}
		});
		panel.add(qureyBtn);
	}
	
	private boolean checkUserInfo(){
		if(!checkTicketInfo()){
			return false;
		}
		if(!checkQueryInfo()){
			return false;
		}
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
			return false;
		}
		String fromStationCode = TicketHelper.getCityCode(fromStation);
		if(TextUtil.isEmpty(fromStationCode)){
			return false;
		}
		//check to station
		String toStation = mToStationInput.getText(); 
		if(TextUtil.isEmpty(toStation)){
			return false;
		}
		String toStationCode = TicketHelper.getCityCode(toStation);
		if(TextUtil.isEmpty(toStationCode)){
			return false;
		}
		//check date
		String date = mDateInput.getText();
		if(TextUtil.isEmpty(date)){
			return false;
		}
		mUserInfo.setFromStationName(fromStation);
		mUserInfo.setFromStationCode(fromStationCode);
		mUserInfo.setToStationName(toStation);
		mUserInfo.setToStationCode(toStationCode);
		mUserInfo.setDate(date);
		mUserInfo.setTrainFilter(mTrainFilterInput.getText());
		mUserInfo.setSeatFilter(mSeatFilterInput.getText());
		Log.i("checkTicketInfo,date="+date);
		return true;
	}
	
	private boolean checkQueryInfo(){
		return true;
	}
	
	public void setTickProcess(RequestProcess process){
		mTicketProcess = process;
	}
}
