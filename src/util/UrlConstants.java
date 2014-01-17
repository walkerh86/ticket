package util;

public class UrlConstants {
	public final static String GET_COOKIE_URL = "https://kyfw.12306.cn/otn/";
	// 鑾峰彇鐧诲綍楠岃瘉鐮乽rl
	public final static String GET_LOGIN_CAPTCHA_URL = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand";
	// 鑾峰彇鐧诲綍楠岃瘉鐮乽rl
	public final static String REQ_GETSUBPASSCODE_URL = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=passenger&rand=randp";
	// 妫€鏌ラ獙璇佺爜url
	public final static String REQ_CHECKCODE_URL = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
	// 鐧诲綍楠岃瘉url
	public final static String GET_LOGIN_AYSN_SUGGEST_URL = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
	// 鐧诲綍url
	public final static String GET_LOGIN_URL = "https://kyfw.12306.cn/otn/login/userLogin";
	// 鏌ヨ浣欑エiniturl
	public final static String REQ_TIKETINIT_URL = "https://kyfw.12306.cn/otn/leftTicket/init";
	// 鏌ヨ浣欑エurl
	public final static String REQ_TIKETSEARCH_URL = "https://kyfw.12306.cn/otn/leftTicket/query";
	// 鎻愪氦杞︾エurl
	public final static String REQ_SUBMITORDER_URL = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
	// 鑾峰彇tokenurl
	public final static String REQ_INITDC_URL = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
	// 妫€鏌ヨ鍗晆rl
	public final static String REQ_CHECKORDER_URL = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
	// 鏌ヨ浣欑エ
	public final static String REQ_QUEUECOUNT_URL = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
	// 鎻愪氦璁㈠崟url
	public final static String REQ_CONFIRMSINGLE_URL = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
	// 鏌ヨ绛夊緟鏃堕棿url
	public final static String REQ_QUERYORDERWAIT_URL = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime";

	// 鐧诲綍璇锋眰鐩稿叧ref
	public final static String REF_LOGINPASSCODE_URL = "https://kyfw.12306.cn/otn/login/init";
	// 鏌ヨ浣欑エinit鐩稿叧ref
	public final static String REF_INITTICKET_URL = "https://kyfw.12306.cn/otn/index/init";
	// 鏌ヨ浣欑エ鐩稿叧ref
	public final static String REF_TICKET_URL = "https://kyfw.12306.cn/otn/leftTicket/init";
	
	public static final String REQ_PASSENGERS_INIT_URL = "https://kyfw.12306.cn/otn/passengers/init";
	public static final String REF_PASSENGERS_INIT_URL = "https://kyfw.12306.cn/otn/index/init";
	
	public static final String REQ_PASSENGERS_QUERY_URL = "https://kyfw.12306.cn/otn/passengers/query";
	
	public static final String REQ_CHECK_USER_URL = "https://kyfw.12306.cn/otn/login/checkUser";
	
	public final static String FILE_LOGIN_CAPTCHA_URL = "captcha_login.jpg";
	public final static String FILE_SUBMIT_CAPTCHA_URL = "captcha_submit.jpg";
}
