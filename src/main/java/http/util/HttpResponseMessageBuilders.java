package http.util;

import com.shimizukenta.http.HttpVersion;

public class HttpResponseMessageBuilders {

	private HttpResponseMessageBuilders() {
			/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final HttpResponseMessageBuilder http1p0 = new HttpResponseMessageBuilderVersion1p0();
		private static final HttpResponseMessageBuilder http1p1 = new HttpResponseMessageBuilderVersion1p1();
	}
	
	public static HttpResponseMessageBuilder get(HttpVersion version) {
		
		switch ( version ) {
		case HTTP1_0:
			return SingletonHolder.http1p0;
			/* break; */
			
		case HTTP1_1:
			return SingletonHolder.http1p1;
			/* break; */
		
		case HTTP2_0:
		default: {
			
			throw new IllegalArgumentException("\"" + version + "\" is not supported");
		}
		}
	}
}
