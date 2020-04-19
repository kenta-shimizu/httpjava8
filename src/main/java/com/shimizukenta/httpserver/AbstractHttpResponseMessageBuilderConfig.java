package com.shimizukenta.httpserver;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractHttpResponseMessageBuilderConfig {
	
	private String hostName;
	
	public AbstractHttpResponseMessageBuilderConfig() {
		
		this.hostName = null;
		
		/* No-thing */
	}
	
	public void hostName(CharSequence hostName) {
		synchronized ( this ) {
			this.hostName = Objects.requireNonNull(hostName).toString();
		}
	}
	
	public Optional<String> hostName() {
		synchronized ( this ) {
			return this.hostName == null ? Optional.empty() : Optional.of(this.hostName);
		}
	}
	

}
