import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class FrameSubmitOrder extends JFrame{
	private JButton mCaptchaImg;
	private JTextField mCaptchaInput;
	private UiActionListener mUiActionListener;
	private TicketInfo mSubmitTicketInfo;
	private JTextArea mLogLabel;
	
	public FrameSubmitOrder(UiActionListener listener, TicketInfo ticketInfo){
		mUiActionListener = listener;
		mSubmitTicketInfo = ticketInfo;
		initFrame();
	}
	
	private void initFrame(){
		setTitle("提交订单");
		setResizable(false);
        setSize(380, 260); 
        setLocationRelativeTo(null); //center in window
        addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) { 
                //System.out.println("Exit when Closed event"); 
                //System.exit(0);
            }
        });
        
        initLayout();
	}
	
	private void initLayout(){
		JPanel panel = new JPanel();
		panel.setLayout(null);		
		this.add(panel);
		
		final int ROW_HEIGHT = 42;
		final int TOP_PADDING = 20;
		final int LEFT_PADDING = 20;
		final int ROW_GAP = 15;
		final int COL_GAP = 10;
		final int LABEL_WIDTH = 60;
		final int INPUT_WIDTH = 180;
		final int CAPTCHA_WIDTH = 78;
		final int ARROW_WIDTH = 200;
		
		int xOffset = LEFT_PADDING; //left padding
		int yOffset = TOP_PADDING; //top padding
		
		JLabel fromStationName = new JLabel(mSubmitTicketInfo.mFromStationName);
		fromStationName.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);		
		panel.add(fromStationName);	
		xOffset += LABEL_WIDTH;
		JLabel arrow = new JLabel("----  "+mSubmitTicketInfo.mStationTrainCode
				+", "+mSubmitTicketInfo.mSeatTypeName+"  --->");
		arrow.setBounds(xOffset, yOffset, ARROW_WIDTH, ROW_HEIGHT);		
		panel.add(arrow);	
		xOffset += ARROW_WIDTH;
		JLabel toStationName = new JLabel(mSubmitTicketInfo.mToStationName);
		toStationName.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);		
		panel.add(toStationName);	
		
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_HEIGHT+ROW_GAP;		
		JLabel captcha = new JLabel();
		captcha.setText("验证码：");
		captcha.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);		
		panel.add(captcha);		
		xOffset += LABEL_WIDTH;
		
		mCaptchaInput = new JTextField();
		mCaptchaInput.setBounds(xOffset, yOffset, INPUT_WIDTH, ROW_HEIGHT);
		mCaptchaInput.addKeyListener(new KeyAdapter(){
			public void keyTyped(final KeyEvent e) {				
				int len = mCaptchaInput.getText().length();
				if(len >= 4){
					if (e.getKeyChar() == KeyEvent.VK_ENTER) { 
						mUiActionListener.onUiAction(UiActionListener.UI_ACTION_TICKET_SUBMIT);
					}
					e.consume(); // 销毁本次输入的字符
				}
			}
		});
		panel.add(mCaptchaInput);
		xOffset += INPUT_WIDTH+COL_GAP;
		
		mCaptchaImg = new JButton();
		mCaptchaImg.setBounds(xOffset, yOffset, CAPTCHA_WIDTH, ROW_HEIGHT);
		mCaptchaImg.setBackground(Color.GRAY);
		mCaptchaImg.setIcon(new ImageIcon("captcha.png"));
		mCaptchaImg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				mUiActionListener.onUiAction(UiActionListener.UI_ACTION_UPDATE_CAPTCHA);
			}
		});
		panel.add(mCaptchaImg);
		
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_HEIGHT+10;
		JButton submitBtn = new JButton("提交订单");
		submitBtn.setBounds(xOffset, yOffset, 100, ROW_HEIGHT);
		submitBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				mUiActionListener.onUiAction(UiActionListener.UI_ACTION_TICKET_SUBMIT);
			}
		});
		panel.add(submitBtn);
		
		//row 5
		xOffset = LEFT_PADDING; // left padding
		yOffset += ROW_HEIGHT + ROW_GAP;
		mLogLabel = new JTextArea();
		mLogLabel.setBackground(new Color(0,0,0,0));
		mLogLabel.setBounds(xOffset, yOffset, 300, ROW_HEIGHT * 2);
		panel.add(mLogLabel);
	}
		
	public void setCaptchaIcon(ImageIcon icon){
		mCaptchaImg.setIcon(icon);
	}
	
	public String getCaptchaCode(){
		return mCaptchaInput.getText();
	}
	
	public void showLog(String... messages){
		String msg = new String();
		for (String message : messages) {
			msg += message+"\n";
		}
		mLogLabel.setText(msg);
	}
}
