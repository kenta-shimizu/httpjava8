package com.shimizukenta.http;

public interface HttpRequestMessageReader {
	public HttpRequestMessage read() throws InterruptedException, HttpReadException;
}
