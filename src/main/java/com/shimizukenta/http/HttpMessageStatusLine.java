package com.shimizukenta.http;

import java.nio.charset.StandardCharsets;

public class HttpMessageStatusLine {
	
	private static final String SP = " ";
	
	private final HttpVersion version;
	private final int statusCode;
	private final String reasonPhrase;
	private final String line;
	
	private HttpMessageStatusLine(HttpVersion version, int statusCode, String reasonPhrase, String line) {
		this.version = version;
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.line = line;
	}
	
	public static HttpMessageStatusLine create(HttpVersion version, int statusCode, CharSequence reasonPhrase) {
		
		String rp = reasonPhrase.toString().trim();
		String line = version.toString() + SP + statusCode + SP + rp;
		
		return new HttpMessageStatusLine(
				version,
				statusCode,
				rp,
				line);
	}
	
	public static HttpMessageStatusLine create(HttpVersion version, HttpStatus status) {
		return create(version, status.code(), status.reasonPhrase());
	}
	
	public static HttpMessageStatusLine fromLine(CharSequence line) throws HttpMessageParseException {
		
		String strLine = line.toString();
		
		String[] ss = strLine.split(SP, 3);
		
		if ( ss.length != 3 ) {
			throw new HttpMessageParseException(parseExceptionReason(strLine));
		}
		
		try {
			
			return new HttpMessageStatusLine(
					HttpVersion.get(ss[0]),
					Integer.parseInt(ss[1]),
					ss[2],
					strLine);
		}
		catch ( NumberFormatException e ) {
			throw new HttpMessageParseException(parseExceptionReason(strLine), e);
		}
		
	}
	
	private static String parseExceptionReason(String line) {
		return "Status-Line parse failed \"" + line + "\"";
	}
	
	public static HttpMessageStatusLine fromBytes(byte[] bs) throws HttpMessageParseException {
		return fromLine(new String(bs, StandardCharsets.US_ASCII));
	}
	
	public HttpVersion version() {
		return version;
	}
	
	public int statusCode() {
		return statusCode;
	}
	
	public String reasonPhrase() {
		return reasonPhrase;
	}
	
	public String line() {
		return line;
	}
	
	@Override
	public String toString() {
		return line;
	}
	
}
