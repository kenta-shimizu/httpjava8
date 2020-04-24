package http.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.shimizukenta.http.HttpMessageParseException;
import com.shimizukenta.http.HttpServerConnectionValue;

import http.HttpServerServiceSupplier;
import http.HttpWriteMessageException;
import http.base.HttpMessageWriter;
import http.base.HttpRequestMessage;
import http.util.HttpServer;

abstract public class HttpApiServer extends HttpServer {
	
	private final List<HttpServerServiceSupplier> apiServerServices = new CopyOnWriteArrayList<>();
	
	public HttpApiServer(HttpApiServerConfig config) {
		super(config);
	}
	
	protected boolean addApiServerService(HttpServerServiceSupplier service) {
		return apiServerServices.add(service);
	}
	
	protected boolean removeApiServerService(HttpServerServiceSupplier service) {
		return apiServerServices.remove(service);
	}
	
	protected List<HttpServerServiceSupplier> apiServerServices() {
		return apiServerServices;
	}
	
	@Override
	protected boolean writeResponseMessage(
			HttpMessageWriter writer
			, HttpRequestMessage request
			, HttpServerConnectionValue connectionValue)
					throws InterruptedException
					, HttpWriteMessageException
					, HttpMessageParseException {
		
		for ( HttpServerServiceSupplier s : apiServerServices() ) {
			if ( s.accept(request) ) {
				return s.tryService(writer, request, connectionValue);
			}
		}
		
		return super.writeResponseMessage(writer, request, connectionValue);
	}
	
}
