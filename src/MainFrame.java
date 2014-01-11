import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class MainFrame extends JFrame{
	public MainFrame(){
		initFrame();
	}
	
	private void initFrame(){
		setTitle("Ö÷´°¿Ú");
		setResizable(true);
        setSize(400, 300); 
        setLocationRelativeTo(null); //center in window
        addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent e) { 
                System.out.println("Exit when Closed event"); 
                System.exit(0);
            }
        });
        
        //initFrameLayout();
	}
}
