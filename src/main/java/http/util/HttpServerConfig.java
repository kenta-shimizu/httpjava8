package http.util;

import http.base.AbstractHttpServerConfig;

public class HttpServerConfig extends AbstractHttpServerConfig {
	
	public static final float defaultReBindSeconds = 5.0F;
	public static final int defaultKeepAliveMax = 100;
	public static final long defaultKeepAliveTimeout = 5;
	
	private float reBindSeconds;
	private int keepAliveMax;
	private long keepAliveTimeout;
	
	private final HttpGeneralFileServerServiceConfig fileConfig = new HttpGeneralFileServerServiceConfig();
	
	public HttpServerConfig() {
		super();
		reBindSeconds = defaultReBindSeconds;
		keepAliveMax = defaultKeepAliveMax;
		keepAliveTimeout = defaultKeepAliveTimeout;
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
	
	public int keepAliveMax() {
		synchronized ( this ) {
			return keepAliveMax;
		}
	}
	
	public void keepAliveMax(int v) {
		synchronized ( this ) {
			this.keepAliveMax = v;
		}
	}
	
	/**
	 * 
	 * @return timeout seconds
	 */
	public long keepAliveTimeout() {
		synchronized ( this ) {
			return keepAliveTimeout;
		}
	}
	
	/**
	 * 
	 * @param timeout_seconds
	 */
	public void keepAliveTimeout(long timeout_seconds) {
		synchronized ( this ) {
			this.keepAliveTimeout = timeout_seconds;
		}
	}
	
	public HttpGeneralFileServerServiceConfig generalFileServerServiceConfig() {
		return fileConfig;
	}
	
}
