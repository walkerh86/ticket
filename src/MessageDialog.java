import javax.swing.JDialog;
import javax.swing.JLabel;


public class MessageDialog extends JDialog{
	private JLabel mMessageLabel;
	private static MessageDialog mThis;
	
	private MessageDialog(){
		setTitle("ÌבÊ¾");
		setResizable(false);
        setSize(300, 80); 
        setLocationRelativeTo(null);
        
        mMessageLabel = new JLabel();
        getContentPane().add(mMessageLabel);
	}
	
	public static MessageDialog getInstance(){
		if(mThis == null){
			mThis = new MessageDialog();
		}
		return mThis;
	}
	
	public void showMessage(String msg){
		mMessageLabel.setText(msg);
		setVisible(true);
	}
}
