import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.HttpHeader;
import net.MyHttpUrlRequest;
import net.StringHttpResponse;

import util.Log;
import util.TextUtil;


public class FrameLogin extends JFrame{
	private JTextField mCaptchaInput;
	private JLabel mCaptchaImg;
	private JTextField mUserNameInput;
	private JPasswordField mUserPwInput;
	private JLabel mResponse;
	
	private UiActionListener mUiActionListener;
	
	private static final String HOST_URL = "kyfw.12306.cn";
	private static final String COOKIE_URL = "https://kyfw.12306.cn/otn/";
	private BlockingQueue<MyHttpUrlRequest> mRequestQueue;
	private UserInfo mUserInfo;
	
	public FrameLogin(UiActionListener listener){
		mUiActionListener = listener;
		initFrame();
	}
	
	private void initFrame(){
		setTitle("登陆");
		setResizable(false);
        setSize(400, 300); 
        setLocationRelativeTo(null); //center in window
        addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) { 
                System.out.println("Exit when Closed event"); 
                System.exit(0);
            }
        });
        
        initFrameLayout();
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
		
		xOffset += INPUT_WIDTH+COL_GAP;
		mResponse = new JLabel();
		mResponse.setBounds(xOffset,yOffset,LABEL_WIDTH,ROW_HEIGHT*2);
		panel.add(mResponse);
		
		yOffset += ROW_HEIGHT;
		
		//row2
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_GAP;
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
		mCaptchaInput.setBounds(xOffset, yOffset, INPUT_WIDTH, ROW_HEIGHT);
		mCaptchaInput.addKeyListener(new KeyAdapter(){
			public void keyTyped(final KeyEvent e) {
				if (mCaptchaInput.getText().length() >= 4) {
					e.consume(); // 销毁本次输入的字符
				}
			}
		});
		panel.add(mCaptchaInput);
		xOffset += INPUT_WIDTH+COL_GAP;
		
		mCaptchaImg = new JLabel();
		mCaptchaImg.setBounds(xOffset, yOffset, CAPTCHA_WIDTH, ROW_HEIGHT);
		mCaptchaImg.setBackground(Color.CYAN);
		mCaptchaImg.setIcon(new ImageIcon("captcha.png"));
		panel.add(mCaptchaImg);
		
		yOffset += ROW_HEIGHT;
		
		//row 4
		xOffset = LEFT_PADDING; // left padding
		yOffset += ROW_GAP;
		JButton loginBtn = new JButton();
		loginBtn.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);
		loginBtn.setText("登陆");
		loginBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				if(checkUserInput()){
					if(mUiActionListener != null){
						mUiActionListener.onUiAction(UiActionListener.UI_ACTION_USER_LOGIN);
					}
				}
			}
		});
		panel.add(loginBtn);
	}
	
	private boolean checkUserInput(){
		if(TextUtil.isEmpty(mUserNameInput.getText())){
			return false;
		}
		if(TextUtil.isEmpty(new String(mUserPwInput.getPassword()))){
			return false;
		}
		
		if(TextUtil.isEmpty(mCaptchaInput.getText()) || mCaptchaInput.getText().length() < 4){
			return false;
		}
		
		saveUserInfo();
		
		return true;
	}
	
	private void saveUserInfo(){
		mUserInfo.setUserName(mUserNameInput.getText());
		mUserInfo.setUserPw(new String(mUserPwInput.getPassword()));
		mUserInfo.setCaptchaCode(mCaptchaInput.getText());
	}
	
	public void setCaptchaIcon(ImageIcon icon){
		mCaptchaImg.setIcon(icon);
	}
	
	public void setUserInfo(UserInfo userInfo){
		mUserInfo = userInfo;
		mUserNameInput.setText(mUserInfo.getUserName());
		mUserNameInput.setCaretPosition(mUserNameInput.getText().length());
		mUserPwInput.setText(mUserInfo.getUserPw());
		//mUserPwInput.setCaretPosition(mUserPwInput.getPassword().length());
	}
	
	public void setResponseResult(String result){
		mResponse.setText(result);
	}
	
	public interface OnLogInListener{
		public void OnLogIn();
	}
}
