package http.util;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import http.HttpLog;
import http.HttpLogListener;
import http.HttpMessageParseException;
import http.HttpServerServiceSupplier;
import http.base.HttpRequestMessage;

abstract public class HttpServerService implements HttpServerServiceSupplier {

	public HttpServerService(HttpServerServiceConfig config) {
		super();
	}
	
	protected static final QueryParser queryParser = QueryParser.getInstance();
	
	protected static String getAbsolutePath(HttpRequestMessage request) throws HttpMessageParseException {
		return (request.requestLine().uri().split("\\?"))[0];
	}
	
	protected static QueryParseResult getQuery(HttpRequestMessage request) throws HttpMessageParseException {
		
		switch ( request.requestLine().method() ) {
		case HEAD:
		case GET: {
			
			String[] ss = request.requestLine().uri().split("\\?", 2);
			if ( ss.length == 2 ) {
				
				return queryParser.parse(ss[1]);
				
			} else {
				
				return queryParser.empty();
			}
			/* break; */
		}
		case POST:
		default: {
			
			return queryParser.parse(new String(request.body().getBytes(), StandardCharsets.UTF_8));
		}
		}
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
