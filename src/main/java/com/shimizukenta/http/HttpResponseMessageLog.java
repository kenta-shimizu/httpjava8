package com.shimizukenta.http;

import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpResponseMessageLog {
	
	private final LocalDateTime timestamp;
	private final SocketAddress localAddr;
	private final SocketAddress remoteAddr;
	private final HttpResponseMessage rspMsg;
	
	private String cacheToString;
	
	private HttpResponseMessageLog(
			SocketAddress localAddress,
			SocketAddress remoteAddress,
			HttpResponseMessage responseMessage) {
		
		this.timestamp = LocalDateTime.now();
		this.localAddr = localAddress;
		this.remoteAddr = remoteAddress;
		this.rspMsg = responseMessage;
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
	
	public HttpResponseMessage responseMessage() {
		return this.rspMsg;
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
				.append(rspMsg.statusLine())
				.append(BR);
				
				rspMsg.headerGroup().lines().forEach(line -> {
					sb.append(line)
					.append(BR);
				});
				
				sb.append(BR);
				sb.append(rspMsg.body().toString());
				
				this.cacheToString = sb.toString();
			}
			
			return this.cacheToString;
		}
	}
	
	public static HttpResponseMessageLog from(
			SocketAddress localAddress,
			SocketAddress remoteAddress,
			HttpResponseMessage responseMessage) {
		
		return new HttpResponseMessageLog(localAddress, remoteAddress, responseMessage);
	}
	
}
