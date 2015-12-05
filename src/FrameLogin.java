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
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import util.Log;
import util.TextUtil;


public class FrameLogin extends JFrame{
	private JTextField mCaptchaInput;
	private JButton mCaptchaImg;
	private JTextField mUserNameInput;
	private JPasswordField mUserPwInput;
	private JTextArea mLogLabel;
	private PanelCapta mPanelCapta;
	
	private UiActionListener mUiActionListener;
	
	private UserInfo mUserInfo;
		
	public FrameLogin(UiActionListener listener){
		mUiActionListener = listener;
		initFrame();
	}
	
	private void initFrame(){
		setTitle("登陆");
		setResizable(false);
        setSize(360, 420); 
        setLocationRelativeTo(null); //center in window
        addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) { 
                System.out.println("Exit when Closed event"); 
                System.exit(0);
            }
        	public void windowOpened(java.awt.event.WindowEvent evt) {
        		checkFocus();
            }
        });
        
        initFrameLayout();
        //checkFocus();
	}
	
	private void initFrameLayout(){
		JPanel panel = new JPanel();
		//BoxLayout boxLayout = new BoxLayout(panel,BoxLayout.Y_AXIS);
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
		final int LOG_WIDTH = 400-LEFT_PADDING*2;
		
		int xOffset = LEFT_PADDING; //left padding
		int yOffset = TOP_PADDING; //top padding
		//row 1
		JLabel userName = new JLabel();
		userName.setText("用户名:");
		userName.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);
		panel.add(userName);		
		xOffset += LABEL_WIDTH;
		
		mUserNameInput = new JTextField();
		mUserNameInput.setBounds(xOffset,yOffset,INPUT_WIDTH,ROW_HEIGHT);
		panel.add(mUserNameInput);
		
		//row2
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_HEIGHT+ROW_GAP;
		JLabel userPw = new JLabel();
		userPw.setText("密码:");
		userPw.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT);
		panel.add(userPw);		
		xOffset += LABEL_WIDTH;
		
		mUserPwInput = new JPasswordField();
		mUserPwInput.setBounds(xOffset,yOffset,INPUT_WIDTH,ROW_HEIGHT);
		panel.add(mUserPwInput);
		yOffset += ROW_HEIGHT;
		
		//row3
		xOffset = LEFT_PADDING; // left padding
		yOffset += ROW_GAP;
		JLabel captcha = new JLabel();
		captcha.setText("验证码：");
		captcha.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);		
		panel.add(captcha);	
		
		xOffset += LABEL_WIDTH;
		mCaptchaInput = new JTextField();
		mCaptchaInput.requestFocusInWindow();
		mCaptchaInput.setBounds(xOffset, yOffset, 110, ROW_HEIGHT);
		mCaptchaInput.addKeyListener(new KeyAdapter(){
			public void keyTyped(final KeyEvent e) {
				int len = mCaptchaInput.getText().length();
				if(len >= 4){
					if (e.getKeyChar() == KeyEvent.VK_ENTER) { 
						doLogin();
					}
					e.consume(); // 销毁本次输入的字符
				}
			}
		});
		panel.add(mCaptchaInput);
		
		
		
		//row 4
		xOffset += 110+10; // left padding
		JButton loginBtn = new JButton();
		loginBtn.setBounds(xOffset, yOffset, 100, ROW_HEIGHT);
		loginBtn.setText("登陆");
		loginBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				doLogin();
			}
		});
		panel.add(loginBtn);

		yOffset += ROW_HEIGHT+10;
		xOffset = LEFT_PADDING;

		mPanelCapta = new PanelCapta(xOffset,yOffset);
		panel.add(mPanelCapta);
		
		//row 5
		yOffset += 190;
		xOffset = LEFT_PADDING; // left padding
		mLogLabel = new JTextArea();
		mLogLabel.setBackground(new Color(0,0,0,0));
		mLogLabel.setBounds(xOffset, yOffset, 260, ROW_HEIGHT*2);
		mLogLabel.setForeground(Color.RED);
		mLogLabel.setLineWrap(true);
		
		panel.add(mLogLabel);
	}
	
	private void checkFocus(){
		if(TextUtil.isEmpty(mUserNameInput.getText())){
			mUserNameInput.requestFocus();
		}
		if(TextUtil.isEmpty(new String(mUserPwInput.getPassword()))){
			mUserNameInput.requestFocus();
		}
		mCaptchaInput.requestFocus();		
	}
	
	public void showLog(String... messages){
		String msg = new String();
		for (String message : messages) {
			msg += message+"\n";
		}
		mLogLabel.setText(msg);
	}
	
	public void clearLog(){
		mLogLabel.setText("");
	}
	
	private void doLogin(){
		showLog("");
		if(checkUserInput()){
			if(mUiActionListener != null){
				mUiActionListener.onUiAction(UiActionListener.UI_ACTION_USER_LOGIN);
			}
		}
	}
	
	private boolean checkUserInput(){
		if(TextUtil.isEmpty(mUserNameInput.getText())){
			showLog("请输入用户名！");
			return false;
		}
		if(TextUtil.isEmpty(new String(mUserPwInput.getPassword()))){
			showLog("请输入密码！");
			return false;
		}
		/*
		if(TextUtil.isEmpty(mCaptchaInput.getText()) || mCaptchaInput.getText().length() < 4){
			showLog("请输入验证码！");
			return false;
		}
		*/
		saveUserInfo();
		
		return true;
	}
	
	private void saveUserInfo(){
		mUserInfo.setUserName(mUserNameInput.getText());
		mUserInfo.setUserPw(new String(mUserPwInput.getPassword()));
		mUserInfo.setCaptchaCode(mPanelCapta.getCaptaRelust());
	}
	
	public void checkCaptchaCodeFail(){
		showLog("验证码错误");
		mCaptchaInput.setText("");
	}
	
	public String getCaptchaCode(){
		//return(mCaptchaInput.getText());
		return mPanelCapta.getCaptaRelust();
	}
	
	public void setCaptchaIcon(ImageIcon icon){
		Log.i("FrameLogin.setCaptchaIcon");
		//mCaptchaImg.setIcon(icon);
		mPanelCapta.setCaptaImg(icon);
	}
	
	public void setUserInfo(UserInfo userInfo){
		mUserInfo = userInfo;
		mUserNameInput.setText(mUserInfo.getUserName());
		mUserNameInput.setCaretPosition(mUserNameInput.getText().length());
		mUserPwInput.setText(mUserInfo.getUserPw());
		//mUserPwInput.setCaretPosition(mUserPwInput.getPassword().length());
	}
	
	public interface OnLogInListener{
		public void OnLogIn();
	}
}
