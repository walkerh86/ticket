import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import util.Log;
import util.TicketInfoConstants;

import net.sf.json.JSONObject;


public class TrainListTableModel extends AbstractTableModel{
	private static final String[] columnNames = {"����","��ֹվ��","��ֹʱ��","��ʱ","һ����","������","����","Ӳ��","����","Ӳ��","��ע"};	
	private static final String[] COLUMN_KEYS = {
		TicketInfoConstants.KEY_STATION_TRAIN_CODE,
		TicketInfoConstants.KEY_FROM_STATION_NAME,
		TicketInfoConstants.KEY_START_TIME,
		TicketInfoConstants.KEY_LAST_TIME,
		SeatInfo.getSeatNumKey(SeatInfo.KEY_ZY),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_ZE),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_RW),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_YW),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_RZ),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_YZ),
		TicketInfoConstants.KEY_START_TIME,
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
		JSONObject train = mTrainList.get(row).getJSONObject("queryLeftNewDTO");
		if(col == 1){
			return TicketInfo.getStartEndStation(train);
		}else if(col == 2){
			return TicketInfo.getStartEndTime(train);
		}else if(col == (columnNames.length-1)){
			return mTrainList.get(row).getString("buttonTextInfo");
		}
		return train.getString(COLUMN_KEYS[col]);
	}
	
	public void updateData(ArrayList<JSONObject> trainList){
		mTrainList = trainList;
		this.fireTableDataChanged();
	}
}
