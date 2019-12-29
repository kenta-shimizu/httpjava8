package http.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

abstract public class HttpServerServiceConfig {
	
	public static final boolean defaultAddTimestamps = true;
	
	private final Collection<String> _hostNames = new HashSet<>();
	private String _serverName;
	private boolean _addTimestamps;

	public HttpServerServiceConfig() {
		this._serverName = null;
		this._addTimestamps = defaultAddTimestamps;
	}
	
	public Collection<String> hostNames() {
		synchronized ( this ) {
			return Collections.unmodifiableCollection(_hostNames);
		}
	}
	
	public boolean hostName(CharSequence cs) {
		synchronized ( this ) {
			return this._hostNames.add(cs.toString());
		}
	}
	
	public Optional<String> serverName() {
		synchronized ( this ) {
			return _serverName == null ? Optional.empty() : Optional.of(_serverName);
		}
	}
	
	public void serverName(CharSequence cs) {
		synchronized ( this ) {
			this._serverName = cs.toString();
		}
	}
	
	public boolean addTimestamps() {
		synchronized ( this ) {
			return _addTimestamps;
		}
	}
	
	public void addTimestamps(boolean f) {
		synchronized ( this ) {
			this._addTimestamps = f;
		}
	}
	
}
