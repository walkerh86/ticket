
public interface UiActionListener {
	public static final int UI_ACTION_USER_LOGIN = 1;
	public static final int UI_ACTION_TICKET_QUERY = 2;
	public static final int UI_ACTION_TICKET_AUTO_QUERY_END = 3;
	public static final int UI_ACTION_TICKET_SUBMIT = 4;
	
	public static final int UI_ACTION_UPDATE_CAPTCHA = 10;
	public static final int UI_ACTION_CHECK_CAPTCHA = 11;
	
	public static final int UI_ACTION_ORDER_QUERY_NO_COMPLETE = 21;
	public static final int UI_ACTION_ORDER_CANCEL = 22;
	
	public void onUiAction(int action);
}
