import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;

import util.Log;
//import InterfaceCommon.onItemCheckedListener;


public class FramePassengerList extends JFrame{
	private JCheckBox[] mPassengersCheckBoxs;
	private InterfaceCommon.onItemCheckedListener mOnItemCheckedListener;
	private PassengerManager mPassengerManager;
	private ArrayList<Passenger> mSelectedPassengers = new ArrayList<Passenger>(20);
	private JPanel mListPanel;
	private JTextField mNameInput;
	private JTextField mIdNoInput;
	
	public FramePassengerList(InterfaceCommon.onItemCheckedListener listener){
		mPassengerManager = PassengerManager.getInstance();
		mPassengerManager.initPassengers(new PassengerManager.OnPassengersGetDoneListener() {
			@Override
			public void OnPassengersGetDone(HashMap<String,Passenger> passengers) {
				int count = passengers.size();
				if(passengers == null || count == 0){
					return;
				}
				mPassengersCheckBoxs = new JCheckBox[count];
				int i = 0;
				for(Map.Entry<String,Passenger> entry : passengers.entrySet()){
					//Passenger passenger = passengers.get(i);
					mPassengersCheckBoxs[i] = new JCheckBox();
					mPassengersCheckBoxs[i].setText(entry.getValue().getName());
					mPassengersCheckBoxs[i].setName(entry.getValue().getIdNo());
					mPassengersCheckBoxs[i].addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent itemEvent) {
							int state = itemEvent.getStateChange();
							Log.i("FrameSeatList,itemListener,state="+state);
							if(mOnItemCheckedListener != null){
								mOnItemCheckedListener.onItemChecked((JCheckBox)itemEvent.getItem(),state == ItemEvent.SELECTED);
							}
						}
					});
					mListPanel.add(mPassengersCheckBoxs[i]);
					i++;
				}
				mListPanel.validate();
				mListPanel.repaint();
			}
		});
		
		mOnItemCheckedListener = listener;
		initFrame();
		initLayout();
	}
	
	private void initFrame(){
		setTitle("乘客");
		setResizable(false);
        setSize(500, 240); 
        setLocationRelativeTo(null); //center in window
	}

	private void initLayout(){
		JPanel rootPanel = new JPanel();
		this.setContentPane(rootPanel);
		rootPanel.setLayout(new BoxLayout(rootPanel,BoxLayout.Y_AXIS));
		
		mListPanel = new JPanel();
		rootPanel.add(mListPanel);
		
		mListPanel.setPreferredSize(new Dimension(480,160));
		mListPanel.setLayout(new GridLayout(3,4));
		/*
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
			mListPanel.add(mSeatCheckBoxs[i]);
		}
		*/
		JPanel addPanel = new JPanel();
		int xOffset = 10;
		JLabel nameLabel = new JLabel("姓名：");
		mNameInput = new JTextField();
		mNameInput.setPreferredSize(new Dimension(80,40));
		JLabel idNoLabel = new JLabel("身份证：");
		mIdNoInput = new JTextField();
		mIdNoInput.setPreferredSize(new Dimension(160,40));
		JButton addBtn = new JButton("添加");
		addBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				Passenger passenger = new Passenger();
				passenger.setName(mNameInput.getText());
				passenger.setIdNo(mIdNoInput.getText());
				mPassengerManager.addSelectPassenger(passenger);
				JCheckBox child = new JCheckBox();
				child.setText(passenger.getName());
				child.setName(passenger.getIdNo());
				child.addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent itemEvent) {
						int state = itemEvent.getStateChange();
						if(mOnItemCheckedListener != null){
							mOnItemCheckedListener.onItemChecked((JCheckBox)itemEvent.getItem(),state == ItemEvent.SELECTED);
						}
					}
				});
				child.setSelected(true);
				mListPanel.add(child);
				mListPanel.validate();
				mListPanel.repaint();
				
				mNameInput.setText("");
				mIdNoInput.setText("");
			}
		});
		addPanel.add(nameLabel);
		addPanel.add(mNameInput);
		addPanel.add(idNoLabel);
		addPanel.add(mIdNoInput);
		addPanel.add(addBtn);
		rootPanel.add(addPanel);
	}
}
