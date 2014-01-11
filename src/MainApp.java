import java.awt.Frame;

public class MainApp{
	private static LoginFrame mLoginFrame;
	private static MainFrame mMainFrame;
	
	public static void main(String args[ ]){
		mLoginFrame = new LoginFrame(); 
		mLoginFrame.setVisible(true); 
		mLoginFrame.setOnLogInListener(new LoginFrame.OnLogInListener() {			
			@Override
			public void OnLogIn() {
				mLoginFrame.setVisible(false); 
				if(mMainFrame == null){
					mMainFrame = new MainFrame();
				}
				mMainFrame.setVisible(true);
			}
		});
	}
}
