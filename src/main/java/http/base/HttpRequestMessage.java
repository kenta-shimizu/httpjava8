package http.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public boolean isKeepAlive() {
		
		try {
			return headerGroup()
					.getFieldValue(HttpHeaderField.Connection)
					.filter(v -> v.equalsIgnoreCase("Keep-Alive"))
					.isPresent();
		}
		catch ( HttpMessageParseException giveup ) {
		}
		
		return false;
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
	
	public List<String> acceptEncodings() {
		
		try {
			return headerGroup().getFieldValue(HttpHeaderField.AcceptEncoding)
					.map(v -> {
						
						/*** HOOK ***/
						
						String[] ss = v.split(",");
						
						List<String> ll = new ArrayList<>();
						
						for ( String s : ss ) {
							
							String[] tt = s.split(";");
							
							String t = tt[0].trim();
							
							if ( ! t.isEmpty() ) {
								ll.add(t);
							}
						}
						
						return ll;
					})
					.orElse(Collections.emptyList());
		}
		catch ( HttpMessageParseException giveup ) {
		}
		
		return Collections.emptyList();
	}
}
