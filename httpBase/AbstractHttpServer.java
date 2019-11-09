package httpBase;

import java.io.Closeable;
import java.io.IOException;

public abstract class AbstractHttpServer extends AbstractHttpTerminalEntity implements Closeable {
	
	public AbstractHttpServer(AbstractHttpServerConfig config) {
		super();
	}
	
	public abstract void open() throws IOException;
	
}
