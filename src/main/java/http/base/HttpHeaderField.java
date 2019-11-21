package http.base;

public enum HttpHeaderField {
	
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
	
	private HttpHeaderField(String fn) {
		this.fieldName = fn;
	}
	
	public String fieldName() {
		return fieldName;
	}
	
	public static HttpHeaderField get(CharSequence cs) {
		
		String s = cs.toString();
		
		for ( HttpHeaderField hfn : values() ) {
			if ( hfn.fieldName.equalsIgnoreCase(s) ) {
				return hfn;
			}
		}
		
		return HttpHeaderField.UNKNOWN;
	}
}
