package com.shimizukenta.httpserver;

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
