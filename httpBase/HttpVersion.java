package httpBase;

public enum HttpVersion {
	
	UNDEFINED("undefined"),
	
	HTTP1_0("HTTP/1.0"),
	HTTP1_1("HTTP/1.1"),
	HTTP2_0("HTTP/2.0"),
	
	;
	
	private final String version;
	
	private HttpVersion(String ver) {
		this.version = ver;
	}
	
	public static HttpVersion get(CharSequence cs) {
		
		String s = cs.toString();
		
		for (HttpVersion v : values()) {
			if ( v.version.equals(s) ) {
				return v;
			}
		}
		
		return HttpVersion.UNDEFINED;
	}
	
	@Override
	public String toString() {
		return version;
	}
}
