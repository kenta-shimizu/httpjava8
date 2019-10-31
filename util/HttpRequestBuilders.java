package util;

public class HttpRequestBuilders {

	private HttpRequestBuilders() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final HttpRequestBuilder httpReqVer1_1 = new HttpRequestBuilderVersion1p1();
	}
	
	public static HttpRequestBuilder getVersion1_1() {
		return SingletonHolder.httpReqVer1_1;
	}
	
}
