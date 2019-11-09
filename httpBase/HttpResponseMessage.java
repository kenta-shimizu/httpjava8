package httpBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponseMessage {
	
	private static final String CRLF = "\r\n";
	private static final byte[] CRLFBYTES = CRLF.getBytes(StandardCharsets.US_ASCII);

	private final HttpStatusLine statusLine;
	private final HttpHeaderGroup headerGroup;
	private final HttpMessageBody body;
	
	private String parsedString;
	private byte[] parsedBytes;

	public HttpResponseMessage(HttpStatusLine statusLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		this.statusLine = statusLine;
		this.headerGroup = headerGroup;
		this.body = body;
		this.parsedString = null;
		this.parsedBytes = null;
	}
	
	public HttpStatusLine statusLine() {
		return statusLine;
	}
	
	public HttpHeaderGroup headerGroup() {
		return headerGroup;
	}
	
	public HttpMessageBody body() {
		return body;
	}
	
	public byte[] getBytes() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( parsedBytes == null ) {
				
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
					
					os.write(statusLine.toString().getBytes(StandardCharsets.US_ASCII));
					os.write(CRLFBYTES);
					
					os.write(headerGroup.getBytes());
					
					os.write(CRLFBYTES);
					
					os.write(body.getBytes());
					
					parsedBytes = os.toByteArray();
				}
				catch ( IOException e ) {
					throw new HttpMessageParseException(e);
				}
			}
			
			return parsedBytes;
		}
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( parsedString == null ) {
				
				parsedString = statusLine.toString() + CRLF
						+ headerGroup.toString()
						+ CRLF
						+ body.toString();
			}
			
			return parsedString;
		}
	}
	
}
