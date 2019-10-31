package httpBase;

import java.net.SocketAddress;

public class HttpServerConfig {
	
	private SocketAddress serverAddress;
	
	public HttpServerConfig() {
		this.serverAddress = null;
	}
	
	public void serverAddress(SocketAddress address) {
		this.serverAddress = address;
	}
	
	public SocketAddress serverAddress() {
		return this.serverAddress;
	}
	
}
