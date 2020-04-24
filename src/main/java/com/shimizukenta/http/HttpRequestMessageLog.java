package com.shimizukenta.http;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpRequestMessageLog {
	
	private final LocalDateTime timestamp;
	private final SocketAddress localAddr;
	private final SocketAddress remoteAddr;
	private final HttpRequestMessage reqMsg;
	
	private String cacheToString;
	
	public HttpRequestMessageLog(
		SocketAddress localAddress,
		SocketAddress remoteAddress,
		HttpRequestMessage requestMessage) {
		
		this.timestamp = LocalDateTime.now();
		this.localAddr = localAddress;
		this.remoteAddr = remoteAddress;
		this.reqMsg = requestMessage;
		this.cacheToString = null;
	}
	
	public LocalDateTime timestamp() {
		return this.timestamp;
	}
	
	public SocketAddress localAddress() {
		return this.localAddr;
	}
	
	public SocketAddress remoteAddress() {
		return this.remoteAddr;
	}
	
	public HttpRequestMessage requestMessage() {
		return this.reqMsg;
	}
	
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "\t";
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( this.cacheToString == null ) {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("timestamp:")
				.append(SPACE)
				.append(timestamp.format(DATETIME))
				.append(BR)
				.append("remote-address:")
				.append(SPACE)
				.append(remoteAddr.toString())
				.append(BR)
				.append(reqMsg.requestLine())
				.append(BR);
				
				reqMsg.headerGroup().lines().forEach(line -> {
					sb.append(line)
					.append(BR);
				});
				
				sb.append(BR);
				sb.append(reqMsg.body().toString());
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
	public static HttpRequestMessageLog from(
			SocketAddress localAddress,
			SocketAddress remoteAddress,
			HttpRequestMessage requestMessage) {
		
		return new HttpRequestMessageLog(localAddress, remoteAddress, requestMessage);
	}
	
}
