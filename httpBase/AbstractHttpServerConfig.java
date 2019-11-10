package httpBase;

import java.net.SocketAddress;

public class AbstractHttpServerConfig {
	
	private SocketAddress serverAddress;
	
	public AbstractHttpServerConfig() {
		this.serverAddress = null;
	}
	
	public void serverAddress(SocketAddress address) {
		synchronized ( this ) {
			this.serverAddress = address;
		}
	}
	
	public SocketAddress serverAddress() {
		synchronized ( this ) {
			return this.serverAddress;
		}
	}
	
}
