package http.base;

import java.net.SocketAddress;

public class AbstractHttpServerConfig {
	
	private SocketAddress _serverAddress;
	
	public AbstractHttpServerConfig() {
		this._serverAddress = null;
	}
	
	public void serverAddress(SocketAddress address) {
		synchronized ( this ) {
			this._serverAddress = address;
		}
	}
	
	public SocketAddress serverAddress() {
		synchronized ( this ) {
			
			if ( _serverAddress == null ) {
				throw new IllegalStateException("Server Address not setted");
			}
			
			return _serverAddress;
		}
	}
	
}
