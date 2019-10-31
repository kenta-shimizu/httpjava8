package httpBase;

public class HttpStatusLine {
	
	private static final String SP = " ";
	
	private HttpVersion version;
	private int statusCode;
	private String reasonPhrase;
	private String line;
	
	public HttpStatusLine(HttpVersion version, int statusCode, CharSequence reasonPhrase) {
		this.version = version;
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase.toString().trim();
		this.line = null;
	}
	
	public HttpStatusLine(HttpVersion version, HttpStatus status) {
		this(version, status.code(), status.reasonPhrase());
	}
	
	public HttpStatusLine(CharSequence line) {
		this.version = null;
		this.statusCode = -1;
		this.reasonPhrase = null;
		this.line = line.toString();
	}
	
	public HttpVersion version() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( version == null ) {
				parse();
			}
			
			return version;
		}
	}
	
	public int statusCode() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( statusCode < 0 ) {
				parse();
			}
			
			return statusCode;
		}
	}
	
	public String reasonPhrase() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( reasonPhrase == null ) {
				parse();
			}
			
			return reasonPhrase;
		}
	}
	
	private void parse() throws HttpMessageParseException {
		
		String[] ss = line.split(SP, 3);
		
		if ( ss.length < 3 ) {
			throw new HttpMessageParseException("Status-Line parse failed");
		}
		
		version = HttpVersion.get(ss[0]);
		reasonPhrase = ss[2];
		
		try {
			statusCode = Integer.parseInt(ss[1]);
		}
		catch ( NumberFormatException e ) {
			throw new HttpMessageParseException("Status-Line parse failed", e);
		}
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( line == null ) {
				
				line = version.toString()
						+ SP
						+ String.format("%03d", statusCode)
						+ SP
						+ reasonPhrase;
			}
			
			return line;
		}
	}
}
