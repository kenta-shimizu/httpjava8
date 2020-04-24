package com.shimizukenta.http;

import java.time.format.DateTimeFormatter;

public class HttpAccessLog {
	
	private final HttpRequestMessageLog request;
	private final HttpResponseMessageLog response;
	
	private String cacheToString;
	
	public HttpAccessLog(HttpRequestMessageLog request, HttpResponseMessageLog response) {
		this.request = request;
		this.response = response;
		this.cacheToString = null;
	}
	
	public HttpRequestMessageLog request() {
		return this.request;
	}
	
	public HttpResponseMessageLog response() {
		return this.response;
	}
	
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "\t";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append(this.request.timestamp().format(DATETIME))
				.append(SPACE)
				.append(this.request.requestMessage().requestLine())
				.append(BR)
				.append(this.response.timestamp().format(DATETIME))
				.append(SPACE)
				.append(this.response.responseMessage().statusLine().statusCode())
				.append(SPACE)
				.append(this.response.responseMessage().body().length())
				.append(SPACE)
				.append(this.response.remoteAddress().toString());
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
}
