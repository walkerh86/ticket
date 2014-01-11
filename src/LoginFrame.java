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


public class LoginFrame extends JFrame{
	private JTextField mCaptchaInput;
	private JLabel mCaptchaImg;
	private JTextField mUserNameInput;
	private JPasswordField mUserPwInput;
	private JLabel mResponse;
	private OnLogInListener mOnLogInListener;
	
	public LoginFrame(){
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
		BoxLayout boxLayout = new BoxLayout(panel,BoxLayout.Y_AXIS);
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
				if(mOnLogInListener != null){
					mOnLogInListener.OnLogIn();
				}
			}
		});
		panel.add(loginBtn);
	}
	
	public void setResponseResult(String result){
		mResponse.setText(result);
	}
	
	public void setOnLogInListener(OnLogInListener listener){
		mOnLogInListener = listener;
	}
	
	public interface OnLogInListener{
		public void OnLogIn();
	}
}
