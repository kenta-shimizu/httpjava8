package com.shimizukenta.http;

public enum HttpMessageHeaderField {
	
	UNKNOWN(""),
	
	AcceptEncoding("Accept-Encoding"),
	AcceptRanges("Accept-Ranges"),
	Connection("Connection"),
	ContentEncoding("Content-Encoding"),
	ContentLength("Content-Length"),
	ContentType("Content-Type"),
	Date("Date"),
	Host("Host"),
	KeepAlive("Keep-Alive"),
	LastModified("Last-Modified"),
	Server("Server"),
	TransferEncoding("Transfer-Encoding"),
	
	Origin("Origin"),
	AccessControlRequestMethod("Access-Control-Request-Method"),
	AccessControlRequestHeaders("Access-Control-Request-Headers"),
	
	AccessControlAllowOrigin("Access-Control-Allow-Origin"),
	AccessControlAllowCredentials("Access-Control-Allow-Credentials"),
	AccessControlAllowMethods("Access-Control-Allow-Methods"),
	AccessControlAllowHeaders("Access-Control-Allow-Headers"),
	AccessControlExposeHeaders("Access-Control-Expose-Headers"),
	AccessControlMaxAge("Access-Control-Max-Age"),
	
	;
	
	private String fieldName;
	
	private HttpMessageHeaderField(String fn) {
		this.fieldName = fn;
	}
	
	public String fieldName() {
		return fieldName;
	}
	
	public static HttpMessageHeaderField get(CharSequence cs) {
		
		if ( cs != null ) {
			
			String s = cs.toString();
			
			for ( HttpMessageHeaderField v : values() ) {
				if ( v.fieldName.equalsIgnoreCase(s) ) {
					return v;
				}
			}
		}
		
		return HttpMessageHeaderField.UNKNOWN;
	}
}
