
public interface UiActionListener {
	public static final int UI_ACTION_USER_LOGIN = 1;
	public static final int UI_ACTION_TICKET_AUTO_QUERY = 2;
	public static final int UI_ACTION_TICKET_SUBMIT = 3;
	
	public void onUiAction(int action);
}
