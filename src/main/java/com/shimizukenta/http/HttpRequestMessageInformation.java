package com.shimizukenta.http;

import java.net.SocketAddress;
import java.time.LocalDateTime;

public class HttpRequestMessageInformation {
	
	private final HttpRequestMessage message;
	private final LocalDateTime timestamp;
	private final SocketAddress localAddr;
	private final SocketAddress remoteAddr;
	
	private final HttpMethod method;
	private final String path;
	private final HttpQuery query;
	private final HttpVersion version;
	
	private final HttpRequestMessageLog log;
	
	public HttpRequestMessageInformation(
			HttpRequestMessage message,
			LocalDateTime timestamp,
			SocketAddress localAddress,
			SocketAddress remoteAddress) {
		
		this.message = message;
		this.timestamp = timestamp;
		this.localAddr = localAddress;
		this.remoteAddr = remoteAddress;
		
		this.method = message.requestLine().method();
		this.path = (message.requestLine().uri().split("\\?", 2))[0];
		this.query = HttpQuery.parse(message);
		this.version = message.requestLine().version();
		
		this.log = new HttpRequestMessageLog(message, timestamp, localAddress, remoteAddress);
	}
	
	public HttpRequestMessage requestMessage() {
		return this.message;
	}
	
	public SocketAddress getLocalAddress() {
		return this.localAddr;
	}
	
	public SocketAddress getRemoteAddress() {
		return this.remoteAddr;
	}
	
	public LocalDateTime timestamp() {
		return this.timestamp;
	}
	
	public HttpRequestMessageLog log() {
		return this.log;
	}
	
	public HttpMethod method() {
		return this.method;
	}
	
	public String path() {
		return this.path;
	}
	
	public HttpQuery query() {
		return this.query;
	}
	
	public HttpVersion version() {
		return this.version;
	}
	
	public static HttpRequestMessageInformation from(
			HttpRequestMessage message,
			SocketAddress localAddress,
			SocketAddress remoteAddress) {
		
		return new HttpRequestMessageInformation(
				message,
				LocalDateTime.now(),
				localAddress,
				remoteAddress);
	}
}
