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
import javax.swing.JTextField;


public class FrameSubmitOrder extends JFrame{
	private JLabel mCaptchaImg;
	private JTextField mCaptchaInput;
	private UiActionListener mUiActionListener;
	
	public FrameSubmitOrder(UiActionListener listener){
		mUiActionListener = listener;
		initFrame();
	}
	
	private void initFrame(){
		setTitle("提交订单");
		setResizable(true);
        setSize(600, 600); 
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
		final int INPUT_WIDTH = 200;
		final int CAPTCHA_WIDTH = 78;
		
		int xOffset = LEFT_PADDING; //left padding
		int yOffset = TOP_PADDING; //top padding
		
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
		
		xOffset = LEFT_PADDING; //left padding
		yOffset += ROW_HEIGHT+10;
		JButton submitBtn = new JButton("提交订单");
		submitBtn.setBounds(xOffset, yOffset, LABEL_WIDTH, ROW_HEIGHT);
		submitBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				//if(checkUserInfo()){
					mUiActionListener.onUiAction(UiActionListener.UI_ACTION_TICKET_SUBMIT);
				//}
			}
		});
		panel.add(submitBtn);
	}
		
	public void setCaptchaIcon(ImageIcon icon){
		mCaptchaImg.setIcon(icon);
	}
	
	public String getCaptchaCode(){
		return mCaptchaInput.getText();
	}
}
