package http.util;

import http.base.AbstractHttpServerConfig;

public class HttpServerConfig extends AbstractHttpServerConfig {
	
	public static final float defaultReBindSeconds = 5.0F;
	public static final int defaultKeepAliveMax = 100;
	public static final long defaultKeepAliveTimeout = 5;
	
	private float _reBindSeconds;
	private int _keepAliveMax;
	private long _keepAliveTimeout;
	
	private final HttpGeneralFileServerServiceConfig _fileConfig = new HttpGeneralFileServerServiceConfig();
	
	public HttpServerConfig() {
		super();
		
		_reBindSeconds = defaultReBindSeconds;
		_keepAliveMax = defaultKeepAliveMax;
		_keepAliveTimeout = defaultKeepAliveTimeout;
	}
	
	public float reBindSeconds() {
		synchronized ( this ) {
			return _reBindSeconds;
		}
	}
	
	public void reBindSeconds(float v) {
		
		synchronized ( this ) {
			
			if ( v <= 0.0F ) {
				throw new IllegalArgumentException("");
			}
			
			_reBindSeconds = v;
		}
	}
	
	public int keepAliveMax() {
		synchronized ( this ) {
			return _keepAliveMax;
		}
	}
	
	public void keepAliveMax(int v) {
		synchronized ( this ) {
			this._keepAliveMax = v;
		}
	}
	
	/**
	 * 
	 * @return timeout seconds
	 */
	public long keepAliveTimeout() {
		synchronized ( this ) {
			return _keepAliveTimeout;
		}
	}
	
	/**
	 * 
	 * @param timeout_seconds
	 */
	public void keepAliveTimeout(long timeout_seconds) {
		synchronized ( this ) {
			this._keepAliveTimeout = timeout_seconds;
		}
	}
	
	public HttpGeneralFileServerServiceConfig generalFileServerServiceConfig() {
		return _fileConfig;
	}
	
}
