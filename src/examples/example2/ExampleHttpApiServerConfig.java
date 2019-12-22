package example2;

import http.api.HttpApiServerConfig;

public class ExampleHttpApiServerConfig extends HttpApiServerConfig {
	
	private final ExampleHttpApiServerServiceConfig apiServerServiceConfig = new ExampleHttpApiServerServiceConfig();
	
	public ExampleHttpApiServerConfig() {
		super();
	}
	
	public ExampleHttpApiServerServiceConfig apiServerServiceConfig() {
		return apiServerServiceConfig;
	}
}
