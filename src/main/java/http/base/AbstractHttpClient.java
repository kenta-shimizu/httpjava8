package http.base;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import http.HttpMessageParseException;
import http.HttpWriteMessageException;

public abstract class AbstractHttpClient extends AbstractHttpTerminalEntity {
	
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
	
	abstract public void request(HttpRequestMessagePack request) throws InterruptedException, HttpWriteMessageException, HttpMessageParseException;
	
}
