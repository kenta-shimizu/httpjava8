package httpBase;

public class HttpRequestLine {
	
	private static final String SP = " ";
	
	private HttpMethod method;
	private String uri;
	private HttpVersion version;
	private String line;
	
	public HttpRequestLine(HttpMethod method, CharSequence uri, HttpVersion version) {
		this.method = method;
		this.uri = uri.toString();
		this.version = version;
		this.line = null;
	}
	
	public HttpRequestLine(CharSequence line) {
		this.method = null;
		this.uri = null;
		this.version = null;
		this.line = line.toString();
	}
	
	public HttpMethod method() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( method == null ) {
				parse();
			}
			
			return method;
		}
	}
	
	public String uri() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( uri == null ) {
				parse();
			}
			
			return uri;
		}
	}
	
	public HttpVersion version() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( version == null ) {
				parse();
			}
			
			return version;
		}
	}
	
	private void parse() throws HttpMessageParseException {
		
		String[] ss = line.split(SP);
		
		if ( ss.length != 3 ) {
			throw new HttpMessageParseException("Request-Line parse failed");
		}
		
		method = HttpMethod.get(ss[0]);
		uri = ss[1];
		version = HttpVersion.get(ss[2]);
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( line == null ) {
				
				line = method.toString()
						+ SP
						+ uri
						+ SP
						+ version.toString();
			}
			
			return line;
		}
	}

}
