package http.api;

import http.HttpMessageParseException;
import http.base.HttpRequestMessage;
import http.util.HttpServerService;

abstract public class HttpApiServerService extends HttpServerService {
	
	private final HttpApiServerServiceConfig config;
	
	public HttpApiServerService(HttpApiServerServiceConfig config) {
		super(config);
		
		this.config = config;
	}

	@Override
	public boolean accept(HttpRequestMessage request) throws HttpMessageParseException {
		return getAbsolutePath(request).equals(config.absolutePath());
	}
	
}
