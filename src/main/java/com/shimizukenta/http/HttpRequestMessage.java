package com.shimizukenta.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRequestMessage extends AbstractHttpMessage {
	
	private static final int OutputStreamSize = 4096;
	
	private final HttpMessageRequestLine requestLine;
	
	private byte[] parsedBytes;
	private String parsedToString;
	
	public HttpRequestMessage(
			HttpMessageRequestLine requestLine,
			HttpMessageHeaderGroup headerGroup,
			AbstractHttpMessageBody body) {
		
		super(headerGroup, body);
		
		this.requestLine = requestLine;
		
		this.parsedBytes = null;
		this.parsedToString = null;
	}
	
	
	private static final String CRLF = "\r\n";
	private static final byte[] CRLFBYTES = CRLF.getBytes(StandardCharsets.US_ASCII);
	
	@Override
	public byte[] getBytes() {
		
		synchronized ( this ) {
			
			if ( this.parsedBytes == null ) {
				
				try (
						ByteArrayOutputStream os = new ByteArrayOutputStream(OutputStreamSize);
						) {
					
					os.write(this.requestLine.line().getBytes(StandardCharsets.US_ASCII));
					os.write(CRLFBYTES);
					
					for ( String line : headerGroup().lines() ) {
						os.write(line.getBytes(StandardCharsets.US_ASCII));
						os.write(CRLFBYTES);
					}
					
					os.write(CRLFBYTES);
					
					os.write(body().getBytes());
					
					this.parsedBytes = os.toByteArray();
				}
				catch ( IOException not_happened ) {
					this.parsedBytes = new byte[0];
				}
			}
			
			return this.parsedBytes;
		}
	}
	
	
	private static final String BR = System.lineSeparator();
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.parsedToString == null ) {
				
				final StringBuilder sb = new StringBuilder();
				
				sb.append(this.requestLine.line())
				.append(BR);
				
				for ( String line : headerGroup().lines() ) {
					sb.append(line)
					.append(BR);
				}
				
				sb.append(BR)
				.append(body().toString());
				
				this.parsedToString = sb.toString();
			}
			
			return this.parsedToString;
		}
	}
	
	public HttpMessageRequestLine requestLine() {
		return requestLine;
	}
	
}
