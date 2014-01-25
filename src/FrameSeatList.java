
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.Log;

public class FrameSeatList extends JFrame{
	private JCheckBox[] mSeatCheckBoxs;
	private onItemCheckedListener mOnItemCheckedListener;
	private HashMap<String,JCheckBox> mSeatViewCache = new HashMap<String,JCheckBox>();
	
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
		HashMap<String,SeatTypeInfo> seatTypeInfo = SeatInfo.getAllSeatTypeInfo();
		for(Map.Entry<String, SeatTypeInfo>entry : seatTypeInfo.entrySet()){
			SeatTypeInfo info = entry.getValue();
			String key = entry.getKey();
			JCheckBox child = new JCheckBox();
			child.setName(key);
			child.setText(info.mName);
			child.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent itemEvent) {
					int state = itemEvent.getStateChange();
					Log.i("FrameSeatList,itemListener,state="+state);
					if(mOnItemCheckedListener != null){
						mOnItemCheckedListener.onItemChecked((JCheckBox)itemEvent.getItem(),state == ItemEvent.SELECTED);
					}
				}
			});
			listPanel.add(child);
			mSeatViewCache.put(key, child);
		}		
	}
	
	public void unselectSeatType(String key){
		if(mSeatViewCache.containsKey(key)){
			JCheckBox child = mSeatViewCache.get(key);
			child.setSelected(false);
		}
	}
	
	public void initSelectedSeats(String[] checkedKeys){
		if(checkedKeys == null) return;
		for(int i=0;i<checkedKeys.length;i++){
			if(mSeatViewCache.containsKey(checkedKeys[i])){
				JCheckBox child = mSeatViewCache.get(checkedKeys[i]);
				child.setSelected(true);
			}
		}
	}
	
	public interface onItemCheckedListener{
		public void onItemChecked(JCheckBox item,boolean checked);
	}
}
