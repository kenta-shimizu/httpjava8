package com.shimizukenta.httpserver;

public interface HttpResponseMessageBuilder {
	
	public HttpVersion version();
	
	default HttpResponseMessage build(
			HttpVersion version,
			HttpStatus status,
			HttpMessageHeaderGroup headerGroup,
			AbstractHttpMessageBody body) {
		
		return new HttpResponseMessage(
				HttpMessageStatusLine.create(version, status),
				headerGroup,
				body);
	}
	
	public HttpResponseMessage build(HttpStatus status);
	
}
