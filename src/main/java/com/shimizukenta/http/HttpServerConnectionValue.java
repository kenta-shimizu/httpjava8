package com.shimizukenta.http;

import java.nio.channels.AsynchronousSocketChannel;

public class HttpServerConnectionValue {
	
	private final AsynchronousSocketChannel channel;
	private final HttpKeepAliveValue keepAlive;
	
	public HttpServerConnectionValue(AsynchronousSocketChannel channel, HttpKeepAliveValue keepAlive) {
		this.channel = channel;
		this.keepAlive = keepAlive;
	}
	
	public AsynchronousSocketChannel channel() {
		return channel;
	}
	
	public HttpKeepAliveValue keepAlive() {
		return keepAlive;
	}

}
