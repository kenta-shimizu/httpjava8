package httpBase;

import java.net.SocketAddress;

public class HttpClientConfig {
	
	private SocketAddress serverAddress;
	
	public HttpClientConfig() {
		this.serverAddress = null;
	}
	
	public void serverAddress(SocketAddress address) {
		this.serverAddress = address;
	}
	
	public SocketAddress serverAddress() {
		return this.serverAddress;
	}

}
