import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import util.TicketInfoConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class OrderListTableModel  extends AbstractTableModel{
	private static final String[] columnNames = {"序号","车次信息","席位信息","旅客信息","票款金额"};	
	private static final String[] COLUMN_KEYS = {
		SeatInfo.getSeatNumKey(SeatInfo.KEY_ZY),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_ZE),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_RW),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_YW),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_RZ),
		SeatInfo.getSeatNumKey(SeatInfo.KEY_YZ),
	};
	private JSONArray mOrderList;
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		if(mOrderList == null){
			return 0;
		}
		//Log.i("getRowCount="+mTrainList.size());
		return mOrderList.size();
	}
	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	public Object getValueAt(int row, int col) {
		if(mOrderList == null){
			return null;
		}
		
		//index
		JSONObject order = null;
		switch(col){
			case 0:
				return row+1;
			case 1:
				order = mOrderList.getJSONObject(row);
				JSONObject stationTrainDTO = order.getJSONObject("stationTrainDTO");
				return order.getString("start_train_date_page")+"开\n"
						+stationTrainDTO.getString("station_train_code")+" "
						+stationTrainDTO.getString("from_station_name")+"-"
						+stationTrainDTO.getString("to_station_name");
			case 2:
				order = mOrderList.getJSONObject(row);
				return order.getString("coach_name")+"车厢\n"
						+order.getString("seat_name")+"\n"
						+order.getString("seat_type_name");
			case 3:
				order = mOrderList.getJSONObject(row);
				JSONObject passengerDTO = order.getJSONObject("passengerDTO");
				return passengerDTO.getString("passenger_name")+"\n"
						+passengerDTO.getString("passenger_id_type_name");
			case 4:
				order = mOrderList.getJSONObject(row);
				return order.getString("ticket_type_name")+" "
						+order.getString("str_ticket_price_page");
		}
				
		return null;
	}
	
	public void updateData(JSONArray orderList){
		mOrderList = orderList;
		this.fireTableDataChanged();
	}
}
