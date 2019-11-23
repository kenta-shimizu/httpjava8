package http.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

abstract public class HttpServerServiceConfig {

	private final Collection<String> hostNames = new HashSet<>();
	private String serverName;
	private boolean addTimestamps;

	public HttpServerServiceConfig() {
		this.serverName = null;
		this.addTimestamps = true;
	}
	
	public Collection<String> hostNames() {
		synchronized ( this ) {
			return Collections.unmodifiableCollection(hostNames);
		}
	}
	
	public boolean hostName(CharSequence cs) {
		synchronized ( this ) {
			return this.hostNames.add(cs.toString());
		}
	}
	
	public Optional<String> serverName() {
		synchronized ( this ) {
			return serverName == null ? Optional.empty() : Optional.of(serverName);
		}
	}
	
	public void serverName(CharSequence cs) {
		synchronized ( this ) {
			this.serverName = cs.toString();
		}
	}
	
	public boolean addTimestamps() {
		synchronized ( this ) {
			return addTimestamps;
		}
	}
	
	public void addTimestamps(boolean f) {
		synchronized ( this ) {
			this.addTimestamps = f;
		}
	}
	
}
