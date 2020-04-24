package com.shimizukenta.http;

public class HttpVersion1p1ResponseMessageBuilderConfig extends AbstractHttpResponseMessageBuilderConfig {
	
	private boolean acceptControlAllowOrigin;
	private boolean accessControlAllowCredentials;
	
	public HttpVersion1p1ResponseMessageBuilderConfig() {
		super();
		
		this.acceptControlAllowOrigin = true;
		this.accessControlAllowCredentials = true;
	}
	
	public void acceptControlAllowOrigin(boolean f) {
		synchronized ( this ) {
			this.acceptControlAllowOrigin = f;
		}
	}
	
	public boolean acceptControlAllowOrigin() {
		synchronized ( this ) {
			return this.acceptControlAllowOrigin;
		}
	}
	
	public void accessControlAllowCredentials(boolean f) {
		synchronized ( this ) {
			this.accessControlAllowCredentials = f;
		}
	}
	
	public boolean accessControlAllowCredentials() {
		synchronized ( this ) {
			return this.accessControlAllowCredentials;
		}
	}
	
}
