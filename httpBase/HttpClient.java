package httpBase;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class HttpClient extends AbstractHttpTerminalEntity {
	
	private final HttpClientConfig config;
	
	public HttpClient(HttpClientConfig config) {
		super();
		this.config = config;
	}
	
	public abstract void request(HttpRequestMessage request);
	
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
	
	protected SocketAddress serverAddress() {
		return config.serverAddress();
	}
	
}
