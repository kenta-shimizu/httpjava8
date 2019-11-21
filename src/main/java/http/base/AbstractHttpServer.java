package http.base;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpServer extends AbstractHttpTerminalEntity {
	
	public AbstractHttpServer(AbstractHttpServerConfig config) {
		super();
	}
	
	private final Collection<HttpLogListener> accessLogListeners = new CopyOnWriteArrayList<>();
	
	public boolean addAccessLogListener(HttpLogListener lstnr) {
		return accessLogListeners.add(lstnr);
	}
	
	public boolean removeAccessLogListener(HttpLogListener lstnr) {
		return accessLogListeners.remove(lstnr);
	}
	
	protected void putAccessLog(HttpLog log) {
		accessLogListeners.forEach(lstnr -> {
			lstnr.receive(log);
		});
	}
	
	private final Collection<HttpLogListener> responseLogListeners = new CopyOnWriteArrayList<>();
	
	public boolean addResponseLogListener(HttpLogListener lstnr) {
		return responseLogListeners.add(lstnr);
	}
	
	public boolean removeResponseLogListener(HttpLogListener lstnr) {
		return responseLogListeners.remove(lstnr);
	}
	
	protected void putResponseLog(HttpLog log) {
		responseLogListeners.forEach(lstnr -> {
			lstnr.receive(log);
		});
	}
}
