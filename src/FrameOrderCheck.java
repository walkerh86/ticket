import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.HttpResponseHandler;
import net.MyHttpResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class FrameOrderCheck extends JFrame{
	OrderListTableModel mOrderListModel;
	private UiActionListener mUiActionListener;
	
	public FrameOrderCheck(UiActionListener listener){
		mUiActionListener = listener;
		
		initFrame();
		initLayout();
	}
	
	private void initFrame(){
		setTitle("未完成订单");
		setResizable(false);
        setSize(600, 400); 
        setLocationRelativeTo(null); //center in window
	}

	private void initLayout(){
		JPanel rootPanel = new JPanel();
		this.setContentPane(rootPanel);
		
		mOrderListModel = new OrderListTableModel();
		JTable orderList = new JTable(mOrderListModel);
		orderList.setRowHeight(80);
		orderList.setPreferredScrollableViewportSize(new Dimension(600, 300));
		orderList.setFillsViewportHeight(true);
		TextAreaCellRenderer tcr = new TextAreaCellRenderer();
		//tcr.setHorizontalAlignment(SwingConstants.CENTER);
		orderList.setDefaultRenderer(Object.class, tcr);
		JScrollPane scrollPane = new JScrollPane(orderList);
		rootPanel.add(scrollPane);
		
		JButton cancelBtn = new JButton("取消订单");
		cancelBtn.setBounds(250, 300, 100, 60);
		cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				mUiActionListener.onUiAction(UiActionListener.UI_ACTION_ORDER_CANCEL);
			}
		});
		rootPanel.add(cancelBtn);
	}
	
	public void updateOrderList(JSONArray orderList){
		mOrderListModel.updateData(orderList);
	}
	
	public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {
		public TextAreaCellRenderer() {
		    setLineWrap(true);
		    setWrapStyleWord(true);
		    //setAlignmentX(CENTER_ALIGNMENT);
		    //setAlignmentY(CENTER_ALIGNMENT);
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value,
		        boolean isSelected, boolean hasFocus, int row, int column) {
		    // 计算当下行的最佳高度
		    int maxPreferredHeight = 0;
		    for (int i = 0; i < table.getColumnCount(); i++) {
		        setText("" + table.getValueAt(row, i));
		        setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
		        maxPreferredHeight = Math.max(maxPreferredHeight,
		                getPreferredSize().height);
		    }
		
		    if (table.getRowHeight(row) != maxPreferredHeight) // 少了这行则处理器瞎忙
		        table.setRowHeight(row, maxPreferredHeight);
		
		    setText(value == null ? "" : value.toString());
		    return this;
		}
	}		
}
