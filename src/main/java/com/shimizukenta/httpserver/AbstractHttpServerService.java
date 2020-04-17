package com.shimizukenta.httpserver;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpServerService implements HttpServerService {

	public AbstractHttpServerService() {
		/* Nothing */
	}
	
	
	private final Collection<HttpLogListener> logListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addLogListener(HttpLogListener l) {
		return logListeners.add(l);
	}

	@Override
	public boolean removeLogListener(HttpLogListener l) {
		return logListeners.remove(l);
	}
	
	protected void putLog(HttpLog log) {
		logListeners.forEach(l -> {l.receive(log);});
	}
	
	private final Collection<HttpResponseMessageLogListener> rspMsgLogListeners = new CopyOnWriteArrayList<>();

	@Override
	public boolean addResponseMessageLogListener(HttpResponseMessageLogListener l) {
		return rspMsgLogListeners.add(l);
	}

	@Override
	public boolean removeResponseMessageLogListener(HttpResponseMessageLogListener l) {
		return rspMsgLogListeners.remove(l);
	}
	
	protected void putResponseMessageLog(HttpResponseMessageLog log) {
		rspMsgLogListeners.forEach(l -> {l.receive(log);});
	}
	
	private final Collection<HttpAccessLogListener> accessLogListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addAccessLogListener(HttpAccessLogListener l) {
		return accessLogListeners.add(l);
	}

	@Override
	public boolean removeAccessLogListener(HttpAccessLogListener l) {
		return accessLogListeners.remove(l);
	}
	
	protected void putAccessLog(HttpAccessLog log) {
		accessLogListeners.forEach(l -> {l.receive(log);});
	}

}
