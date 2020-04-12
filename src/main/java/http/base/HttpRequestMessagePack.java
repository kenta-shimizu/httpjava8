package http.base;

import java.net.SocketAddress;

import com.shimizukenta.http.HttpRequestLine;

public class HttpRequestMessagePack extends HttpRequestMessage {
	
	private final SocketAddress serverSocketAddress;
	
	public HttpRequestMessagePack(
			SocketAddress serverSocketAddress
			, HttpRequestLine requestLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		
		super(requestLine, headerGroup, body);
		
		this.serverSocketAddress = serverSocketAddress;
	}
	
	public SocketAddress serverSocketAddress() {
		return serverSocketAddress;
	}
	
	public HttpRequestMessage requestMessage() {
		return this;
	}
}
