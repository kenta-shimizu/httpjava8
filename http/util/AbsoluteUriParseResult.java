package http.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class AbsoluteUriParseResult {
	
	private final String absoluteUri;
	private final String protocol;
	private final String serverAddress;
	private final int serverPort;
	private final String requestLineUri;
	private final String headerHost;
	
	private SocketAddress socketAddr;
	
	public AbsoluteUriParseResult(
			String absoluteUri
			, String protocol
			, String serverAddress
			, int serverPort
			, String requestLineUri
			, String headerHost) {
		
		this.absoluteUri = absoluteUri;
		this.protocol = protocol;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.requestLineUri = requestLineUri;
		this.headerHost = headerHost;
		
		this.socketAddr = null;
	}
	
	public String absoluteUri() {
		return absoluteUri;
	}
	
	public String protocol() {
		return protocol;
	}
	
	public SocketAddress serverSocketAddress() {
		
		synchronized ( this ) {
			
			if ( socketAddr == null ) {
				
				socketAddr = new InetSocketAddress(serverAddress, serverPort);
			}
			
			return socketAddr;
		}
	}
	
	public String requestLineUri() {
		return requestLineUri;
	}
	
	public String headerHost() {
		return headerHost;
	}
	
}
