package httpBase;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

public abstract class AbstractHttpServer extends AbstractHttpTerminalEntity implements Closeable {
	
	private final AbstractHttpServerConfig config;
	
	public AbstractHttpServer(AbstractHttpServerConfig config) {
		super();
		this.config = config;
	}
	
	protected SocketAddress serverAddress() {
		return config.serverAddress();
	}
	
	public abstract void open() throws IOException;
	
}
