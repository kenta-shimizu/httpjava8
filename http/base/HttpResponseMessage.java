package http.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponseMessage extends AbstractHttpMessage {
	
	private final HttpStatusLine statusLine;
	
	private String parsedString;
	private byte[] parsedBytes;
	
	public HttpResponseMessage(HttpStatusLine statusLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		super(headerGroup, body);
		this.statusLine = statusLine;
		this.parsedString = null;
		this.parsedBytes = null;
	}
	
	public HttpStatusLine statusLine() {
		return statusLine;
	}
	
	public boolean keepAlive() {
		
		try {
			switch ( statusLine().version() ) {
			case HTTP1_0:
				return false;
				/* break; */
			
			default: {
				return headerGroup().getFieldValue(HttpHeaderField.Connection)
						.filter(c -> c.equalsIgnoreCase("Keep-Alive"))
						.isPresent();
			}
			}
		}
		catch ( HttpMessageParseException e ) {
			return false;
		}
		
	}
	
	public byte[] getBytes() throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			if ( parsedBytes == null ) {
				
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
					
					os.write(statusLine.toString().getBytes(StandardCharsets.US_ASCII));
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
				
				parsedString = statusLine.toString() + CRLF
						+ headerGroup().toString()
						+ CRLF
						+ body().toString();
			}
			
			return parsedString;
		}
	}
	
}
