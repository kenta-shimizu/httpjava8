package com.shimizukenta.http;

public abstract class AbstractHttpMessage {
	
	private final HttpMessageHeaderGroup headerGroup;
	private final AbstractHttpMessageBody body;
	
	public AbstractHttpMessage(HttpMessageHeaderGroup headerGroup, AbstractHttpMessageBody body) {
		this.headerGroup = headerGroup;
		this.body = body;
	}
	
	abstract public byte[] getBytes();
	
	
	public HttpMessageHeaderGroup headerGroup() {
		return headerGroup;
	}
	
	public AbstractHttpMessageBody body() {
		return body;
	}
	
}
