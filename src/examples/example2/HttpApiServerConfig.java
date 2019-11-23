package example2;

import http.util.HttpServerConfig;

public class HttpApiServerConfig extends HttpServerConfig {

	private final HttpApiServerServiceConfig apiConfig = new HttpApiServerServiceConfig();
	
	public HttpApiServerConfig() {
		super();
	}
	
	public HttpApiServerServiceConfig apiServerServiceConfig() {
		return apiConfig;
	}

}
