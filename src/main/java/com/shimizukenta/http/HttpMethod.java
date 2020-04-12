package com.shimizukenta.http;

public enum HttpMethod {
	
	UNKNOWN("UNKNOWN"),
	
	OPTIONS("OPTIONS"),
	GET("GET"),
	HEAD("HEAD"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE"),
	TRACE("TRACE"),
	CONNECT("CONNECT"),
	
	;
	
	private final String method;
	
	private HttpMethod(String method) {
		this.method = method;
	}
	
	public static HttpMethod get(CharSequence cs) {
		
		String s = cs.toString();
		
		for ( HttpMethod t : values() ) {
			if ( t.method.equals(s) ) {
				return t;
			}
		}
		
		return HttpMethod.UNKNOWN;
	}
	
	public String toString() {
		return method;
	}
	
}
