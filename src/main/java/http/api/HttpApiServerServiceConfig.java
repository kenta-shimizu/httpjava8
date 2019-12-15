package http.api;

import http.util.HttpServerServiceConfig;

public class HttpApiServerServiceConfig extends HttpServerServiceConfig {
	
	private String absPath;
	
	public HttpApiServerServiceConfig() {
		super();
		
		this.absPath = null;
	}
	
	public void absolutePath(CharSequence absolutePath) {
		synchronized ( this ) {
			this.absPath = absolutePath.toString();
		}
	}
	
	public String absolutePath() {
		synchronized ( this ) {
			if ( absPath == null ) {
				throw new IllegalStateException("Absolute Path not setted");
			}
			
			return absPath;
		}
	}
	
}
