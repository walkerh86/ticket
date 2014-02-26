import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import util.Log;
import util.TicketInfoConstants;

import net.sf.json.JSONObject;


public class TrainListTableModel extends AbstractTableModel{
	private static final String[] columnNames = {"车次","发车时间","到站时间","一等座","二等座","软卧","硬卧","软座","硬座"};	
	private static final String[] COLUMN_KEYS = {
		TicketInfoConstants.KEY_STATION_TRAIN_CODE,
		TicketInfoConstants.KEY_START_TIME,
		TicketInfoConstants.KEY_ARRIVE_TIME,
		SeatInfo.getSeatNumKey(SeatInfo.KEY_ZY),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_ZE),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_RW),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_YW),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_RZ),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_YZ),
	};
	private ArrayList<JSONObject> mTrainList;
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		if(mTrainList == null){
			return 0;
		}
		//Log.i("getRowCount="+mTrainList.size());
		return mTrainList.size();
	}
	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	public Object getValueAt(int row, int col) {
		if(mTrainList == null){
			return null;
		}
		JSONObject train = mTrainList.get(row);
		return train.getString(COLUMN_KEYS[col]);
	}
	
	public void updateData(ArrayList<JSONObject> trainList){
		mTrainList = trainList;
		this.fireTableDataChanged();
	}
}
