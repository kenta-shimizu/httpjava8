package http.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRequestMessage extends AbstractHttpMessage {
	
	private final HttpRequestLine requestLine;
	
	private String parsedString;
	private byte[] parsedBytes;
	
	public HttpRequestMessage(HttpRequestLine requestLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		super(headerGroup, body);
		this.requestLine = requestLine;
		this.parsedString = null;
		this.parsedBytes = null;
	}
	
	public HttpRequestLine requestLine() {
		return requestLine;
	}
	
	@Override
	public byte[] getBytes() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( parsedBytes == null ) {
				
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
					
					os.write(requestLine.toString().getBytes(StandardCharsets.US_ASCII));
					os.write(CRLFBYTES);
					
					os.write(headerGroup().getBytes());
					
					os.write(CRLFBYTES);
					
					os.write(body().getBytes());
					
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
						+ headerGroup().toString()
						+ CRLF
						+ body().toString();
			}
			
			return parsedString;
		}
	}
	
}