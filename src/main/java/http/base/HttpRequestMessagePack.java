package http.base;

import java.net.SocketAddress;

import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageRequestLine;

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
