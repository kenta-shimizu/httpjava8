package http.base;

import java.net.SocketAddress;

import com.shimizukenta.httpserver.HttpMessageHeaderGroup;
import com.shimizukenta.httpserver.HttpMessageRequestLine;

public class HttpRequestMessagePack extends HttpRequestMessage {
	
	private final SocketAddress serverSocketAddress;
	
	public HttpRequestMessagePack(
			SocketAddress serverSocketAddress
			, HttpMessageRequestLine requestLine, HttpMessageHeaderGroup headerGroup, HttpMessageBody body) {
		
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
