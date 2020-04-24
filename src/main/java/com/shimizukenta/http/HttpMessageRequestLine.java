package com.shimizukenta.http;

import java.nio.charset.StandardCharsets;

public class HttpMessageRequestLine {
	
	private static final String SP = " ";
	
	private final HttpMethod method;
	private final String uri;
	private final HttpVersion version;
	private final String line;
	
	private HttpMessageRequestLine(HttpMethod method, String uri, HttpVersion version, String line) {
		this.method = method;
		this.uri = uri;
		this.version = version;
		this.line = line;
	}
	
	public static HttpMessageRequestLine create(HttpMethod method, CharSequence uri, HttpVersion version) {
		
		String strUri = uri.toString();
		
		String line = method.toString()
				+ SP
				+ strUri
				+ SP
				+ version.toString();
		
		return new HttpMessageRequestLine(method, strUri, version, line);
	}
	
	public static HttpMessageRequestLine fromLine(CharSequence line) throws HttpMessageParseException {
		
		String strLine = line.toString();
		
		String[] ss = strLine.split(SP);
		
		if ( ss.length != 3 ) {
			throw new HttpMessageParseException("Request-Line parse failed \"" + strLine + "\"");
		}
		
		return new HttpMessageRequestLine(
				HttpMethod.get(ss[0]),
				ss[1],
				HttpVersion.get(ss[2]),
				strLine);
	}
	
	public static HttpMessageRequestLine fromBytes(byte[] bs) throws HttpMessageParseException {
		return fromLine(new String(bs, StandardCharsets.US_ASCII));
	}
	
	public HttpMethod method() {
		return method;
	}
	
	public String uri() {
		return uri;
	}
	
	public HttpVersion version() {
		return version;
	}
	
	public String line() {
		return line;
	}
	
	@Override
	public String toString() {
		return line;
	}
	
}
