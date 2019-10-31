package httpBase;

public enum HttpHeaderField {
	
	UNKNOWN(""),
	
	Host("Host"),
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
