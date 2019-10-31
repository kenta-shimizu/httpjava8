package httpBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRequestMessage {
	
	private static final String CRLF = "\r\n";
	private static final byte[] CRLFBYTES = CRLF.getBytes(StandardCharsets.US_ASCII);
	
	private final HttpRequestLine requestLine;
	private final HttpHeaderGroup headerGroup;
	private final HttpMessageBody body;
	
	private String parsedString;
	private byte[] parsedBytes;
	
	public HttpRequestMessage(HttpRequestLine requestLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		this.requestLine = requestLine;
		this.headerGroup = headerGroup;
		this.body = body;
		this.parsedString = null;
		this.parsedBytes = null;
	}
	
	public byte[] getBytes() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( parsedBytes == null ) {
				
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
					
					os.write(requestLine.toString().getBytes(StandardCharsets.US_ASCII));
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
				
				parsedString = requestLine.toString() + CRLF
						+ headerGroup.toString()
						+ CRLF
						+ body.toString();
			}
			
			return parsedString;
		}
	}
	
}
