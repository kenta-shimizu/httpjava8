package httpBase;

import java.nio.charset.StandardCharsets;

public class HttpHeader implements Cloneable {
	
	private static final String COLON = ":";
	private static final String SP = " ";
	
	private String fieldName;
	private String fieldValue;
	private String line;
	
	public HttpHeader(CharSequence fieldName, CharSequence fieldValue) {
		this.fieldName = fieldName.toString();
		this.fieldValue = removeWhiteSpace(fieldValue);
		this.line = null;
	}
	
	public HttpHeader(HttpHeaderField field, CharSequence fieldValue) {
		this(field.fieldName(), fieldValue);
	}
	
	public HttpHeader(CharSequence line) {
		this.fieldName = null;
		this.fieldValue = null;
		this.line = line.toString();
	}
	
	public HttpHeader(byte[] bs) {
		this(new String(bs, StandardCharsets.US_ASCII));
	}
	
	@Override
	public Object clone() {
		
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
	
	public String fieldName() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( fieldName == null ) {
				parse();
			}
			
			return fieldName;
		}
	}
	
	public String fieldValue() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( fieldValue == null ) {
				parse();
			}
			
			return fieldValue;
		}
	}
	
	private void parse() throws HttpMessageParseException {
		
		String[] ss = line.split(COLON, 2);
		
		if ( ss.length < 2 ) {
			throw new HttpMessageParseException("Http-Header parse failed");
		}
		
		fieldName = ss[0];
		fieldValue = removeWhiteSpace(ss[1]);
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( line == null ) {
				
				line = fieldName
						+ COLON
						+ SP
						+ fieldValue;
			}
			
			return line;
		}
	}
	
	private static String removeWhiteSpace(CharSequence v) {
		return v.toString().trim().replaceAll("\\s+", SP);
	}
	
	private static class SingletonHolder {
		private static final HttpHeader connectionKeepAlive = new HttpHeader(HttpHeaderField.Connection, "Keep-Alive");
		private static final HttpHeader connectionClose = new HttpHeader(HttpHeaderField.Connection, "close");
	}
	
	public static HttpHeader connectionKeepAlive() {
		return SingletonHolder.connectionKeepAlive;
	}
	
	public static HttpHeader connectionClose() {
		return SingletonHolder.connectionClose;
	}
	
}
