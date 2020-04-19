package com.shimizukenta.httpserver;

public class HttpResponseMessageBuilders {

	private HttpResponseMessageBuilders() {
		/* No-thing */
	}
	
	private static class SingletonHolder {
		private static final HttpResponseMessageBuilders inst = new HttpResponseMessageBuilders();
	}
	
	public static HttpResponseMessageBuilders getInstance() {
		return SingletonHolder.inst;
	}
	
	

}
