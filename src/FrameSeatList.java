
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Log;

public class FrameSeatList extends JFrame{
	JCheckBox[] mSeatCheckBoxs;
	onItemCheckedListener mOnItemCheckedListener;
	
	public FrameSeatList(onItemCheckedListener listener){
		mOnItemCheckedListener = listener;
		initFrame();
		initLayout();
	}
	
	private void initFrame(){
		setTitle("Ï¯±ð");
		setResizable(false);
        setSize(400, 200); 
        setLocationRelativeTo(null); //center in window
	}

	private void initLayout(){
		JPanel listPanel = new JPanel();
		this.setContentPane(listPanel);
		
		listPanel.setLayout(new GridLayout(3,4));
		mSeatCheckBoxs = SeatInfo.getAllSeatTypeCheckBoxs();
		for(int i=0;i<mSeatCheckBoxs.length;i++){
			mSeatCheckBoxs[i].addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent itemEvent) {
					int state = itemEvent.getStateChange();
					Log.i("FrameSeatList,itemListener,state="+state);
					if(mOnItemCheckedListener != null){
						mOnItemCheckedListener.onItemChecked((JCheckBox)itemEvent.getItem(),state == ItemEvent.SELECTED);
					}
				}
			});
			listPanel.add(mSeatCheckBoxs[i]);
		}
	}
	
	public interface onItemCheckedListener{
		public void onItemChecked(JCheckBox item,boolean checked);
	}
}
