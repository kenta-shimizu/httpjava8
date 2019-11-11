package http.base;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpTerminalEntity {

	public AbstractHttpTerminalEntity() {
		/* Nothing */
	}
	
	
	private final Collection<HttpLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	public boolean addLogListener(HttpLogListener lstnr) {
		return logListeners.add(lstnr);
	}
	
	public boolean removeLogListener(HttpLogListener lstnr) {
		return logListeners.remove(lstnr);
	}
	
	protected void putLog(HttpLog log) {
		logListeners.forEach(lstnr -> {
			lstnr.receive(log);
		});
	}
	
	protected void putLog(Throwable t) {
		putLog(new HttpLog(t));
	}
	
}
