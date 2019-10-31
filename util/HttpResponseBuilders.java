package util;

import httpBase.HttpVersion;

public class HttpResponseBuilders {

	private HttpResponseBuilders() {
		/* Nothing */
	}
	
	private static class SingletonHolderHttpResVer1_0 {
		private static final HttpResponseBuilder inst = new HttpResponseBuilderVersion1p0();
	}
	
	public static HttpResponseBuilder getVersion1_0() {
		return SingletonHolderHttpResVer1_0.inst;
	}
	
	private static class SingletonHolderHttpResVer1_1 {
		private static final HttpResponseBuilder inst = new HttpResponseBuilderVersion1p1();
	}

	public static HttpResponseBuilder getVersion1_1() {
		return SingletonHolderHttpResVer1_1.inst;
	}
	
	private static class SingletonHolderHttpResVer2_0 {
		private static final HttpResponseBuilder inst = new HttpResponseBuilderVersion2p0();
	}

	public static HttpResponseBuilder getVersion2_0() {
		return SingletonHolderHttpResVer2_0.inst;
	}
	
	private static class SingletonHolderHttpResVerUndefined {
		private static final HttpResponseBuilder inst = new HttpResponseBuilderVersionUndefined();
	}
	
	public static HttpResponseBuilder getVersionUndefined() {
		return SingletonHolderHttpResVerUndefined.inst;
	}
	
	public static HttpResponseBuilder get(HttpVersion version) {
		
		switch ( version ) {
		case VER1_0 : {
			return getVersion1_0();
		}
		case VER1_1 : {
			return getVersion1_1();
		}
		case VER2_0 : {
			return getVersion2_0();
		}
		default: {
			return getVersionUndefined();
		}
		}
	}
}
