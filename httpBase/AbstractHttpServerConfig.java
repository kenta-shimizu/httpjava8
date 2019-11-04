package httpBase;

import java.net.SocketAddress;

public class AbstractHttpServerConfig {
	
	private SocketAddress serverAddress;
	
	public AbstractHttpServerConfig() {
		this.serverAddress = null;
	}
	
	public void serverAddress(SocketAddress address) {
		this.serverAddress = address;
	}
	
	public SocketAddress serverAddress() {
		return this.serverAddress;
	}
	
}
