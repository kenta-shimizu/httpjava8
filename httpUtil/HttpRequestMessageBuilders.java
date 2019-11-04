package httpUtil;

import httpBase.HttpVersion;

public class HttpRequestMessageBuilders {

	private HttpRequestMessageBuilders() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static HttpRequestMessageBuilder http1p0 = new HttpRequestMessageBuilderVersion1p0();
		private static HttpRequestMessageBuilder http1p1 = new HttpRequestMessageBuilderVersion1p1();
	}
	
	public static HttpRequestMessageBuilder get(HttpVersion version) {
		
		switch ( version ) {
		case HTTP1_0:
			return SingletonHolder.http1p0;
			/* break */
			
		case HTTP1_1:
			return SingletonHolder.http1p1;
			/* break */
			
		case HTTP2_0:
		default: {
			
			throw new IllegalArgumentException("\"" + version + "\" is not supported");
		}
		}
	}
}
