package httpBase;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpClient extends AbstractHttpTerminalEntity implements Closeable {
	
	public AbstractHttpClient(AbstractHttpClientConfig config) {
		super();
	}
	
	private final Collection<HttpResponseMessagePackListener> rspMsgPackListeners = new CopyOnWriteArrayList<>();
	
	public boolean addResponseMessagePackListener(HttpResponseMessagePackListener lstnr) {
		return rspMsgPackListeners.add(lstnr);
	}
	
	public boolean removeResponseMessagePackListener(HttpResponseMessagePackListener lstnr) {
		return rspMsgPackListeners.remove(lstnr);
	}
	
	protected void putResponseMessagePack(HttpResponseMessagePack msgPack) {
		rspMsgPackListeners.forEach(lstnr -> {
			lstnr.receive(msgPack);
		});
	}
	
	abstract public void request(HttpRequestMessage request) throws HttpWriteMessageException;
	abstract public void open() throws IOException;
	
}
