package httpUtil;

import httpBase.AbstractHttpClientConfig;

public class HttpClientConfig extends AbstractHttpClientConfig {
	
	private static final int defaultMaxConnections = 1;
	
	private int maxConnections;
	
	public HttpClientConfig() {
		super();
		
		this.maxConnections = defaultMaxConnections;
	}
	
	public int maxConnection() {
		synchronized ( this ) {
			return maxConnections;
		}
	}
	
	public void maxConnection(int connections) {
		synchronized ( this ) {
			
			if ( connections < 1 ) {
				throw new IllegalArgumentException("value is >= 1");
			}
			
			this.maxConnections = connections;
		}
	}
	
	
}
