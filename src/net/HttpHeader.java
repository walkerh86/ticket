package net;

import java.util.LinkedHashMap;
import java.util.Map;

import util.UrlConstants;

public class HttpHeader {
	private static String ACCEPT = "Accept";

	private static String ACCEPTENCODING = "Accept-Encoding";

	private static String ACCEPTLANGUAGE = "Accept-Language";

	private static String CONNECTION = "Connection";

	private static String HOST = "Host";

	private static String REFERER = "Referer";

	private static String USERAGENT = "User-Agent";

	private static String CACHECONTROL = "Cache-Control";

	private static String XREQUESTEDWITH = "x-requested-with";

	private static String CONTENTTYPE = "Content-Type";

	private static String ACCEPTENCODING_VALUE = "gzip, deflate";

	private static String ACCEPTLANGUAGE_VALUE = "zh-cn";

	private static String CONNECTION_VALUE = "Keep-Alive";

	private static String CACHECONTROL_VALUE = "no-cache";

	private static String CONTENTTYPE_UTF8_VALUE = "application/x-www-form-urlencoded; charset=UTF-8";

	private static String CONTENTTYPE_VALUE = "application/x-www-form-urlencoded";

	private static String HOST_VALUE = "kyfw.12306.cn";

	private static String XREQUESTEDWITH_VALUE = "XMLHttpRequest";

	private static String USERAGENT_VALUE = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";

	public static Map<String, String> getHeader(String referUrl) {
		Map<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put(ACCEPT, "*/*");
		headerMap.put(ACCEPTENCODING, ACCEPTENCODING_VALUE);
		headerMap.put(ACCEPTLANGUAGE, ACCEPTLANGUAGE_VALUE);
		headerMap.put(CACHECONTROL, CACHECONTROL_VALUE);
		headerMap.put(CONNECTION, CONNECTION_VALUE);
		headerMap.put(HOST, HOST_VALUE);
		if(referUrl != null){
			headerMap.put(REFERER, referUrl);
		}
		headerMap.put(USERAGENT, USERAGENT_VALUE);
		headerMap.put(XREQUESTEDWITH, XREQUESTEDWITH_VALUE);
		return headerMap;
	}
}
