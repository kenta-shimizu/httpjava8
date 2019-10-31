package httpBase;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

public abstract class HttpServer extends AbstractHttpTerminalEntity implements Closeable {
	
	private final HttpServerConfig config;
	
	public HttpServer(HttpServerConfig config) {
		super();
		this.config = config;
	}
	
	protected SocketAddress serverAddress() {
		return config.serverAddress();
	}
	
	public abstract void open() throws IOException;
	
}
