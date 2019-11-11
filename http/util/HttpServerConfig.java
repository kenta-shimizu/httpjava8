package http.util;

import http.base.AbstractHttpServerConfig;

public class HttpServerConfig extends AbstractHttpServerConfig {
	
	private static final float defaultReBindSeconds = 5.0F;
	
	private float reBindSeconds;
	
	public HttpServerConfig() {
		super();
		reBindSeconds = defaultReBindSeconds;
	}
	
	public float reBindSeconds() {
		synchronized ( this ) {
			return reBindSeconds;
		}
	}
	
	public void reBindSeconds(float v) {
		
		synchronized ( this ) {
			
			if ( v <= 0.0F ) {
				throw new IllegalArgumentException("");
			}
			
			reBindSeconds = v;
		}
	}

}
