package http.api;

import http.util.HttpServerServiceConfig;

public class HttpApiServerServiceConfig extends HttpServerServiceConfig {
	
	private String _absPath;
	
	public HttpApiServerServiceConfig() {
		super();
		
		this._absPath = null;
	}
	
	public void absolutePath(CharSequence absolutePath) {
		synchronized ( this ) {
			this._absPath = absolutePath.toString();
		}
	}
	
	public String absolutePath() {
		synchronized ( this ) {
			if ( _absPath == null ) {
				throw new IllegalStateException("Absolute Path not setted");
			}
			
			return _absPath;
		}
	}
	
}
