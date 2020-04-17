package com.shimizukenta.httpserver;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHttpServer implements HttpServer {
	
	public AbstractHttpServer() {
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
	
	
	private final Collection<HttpRequestMessageLogListener> reqMsgLogListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addRequestMessageLogListener(HttpRequestMessageLogListener l) {
		return reqMsgLogListeners.add(l);
	}
	
	@Override
	public boolean removeRequestMessageLogListener(HttpRequestMessageLogListener l) {
		return reqMsgLogListeners.remove(l);
	}
	
	protected void putRequestMessageLog(HttpRequestMessageLog log) {
		reqMsgLogListeners.forEach(l -> {l.receive(log);});
	}
	
	
	private final Collection<HttpResponseMessageLogListener> resMsgLogListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addResponseMessageLogListener(HttpResponseMessageLogListener l) {
		return resMsgLogListeners.add(l);
	}
	
	@Override
	public boolean removeResponseMessageLogListener(HttpResponseMessageLogListener l) {
		return resMsgLogListeners.remove(l);
	}
	
	protected void putResponseMessageLog(HttpResponseMessageLog log) {
		resMsgLogListeners.forEach(l -> {l.receive(log);});
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
