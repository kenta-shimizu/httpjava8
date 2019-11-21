package http.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HttpGeneralFileServerServiceConfig extends HttpServerServiceConfig {
	
	private static final int defaultCompressBodyDataSize = 256 * 16;
	
	private Path serverRoot;
	private final Collection<String> hostNames = new HashSet<>();
	private final List<String> directoryIndexes = new ArrayList<>();
	private String serverName;
	private long maxFileSize;
	private boolean addTimestamps;
	private int compressBodyDataSize;
	
	public HttpGeneralFileServerServiceConfig() {
		super();
		
		this.serverRoot = null;
		this.serverName = null;
		this.maxFileSize = (long)(Integer.MAX_VALUE);
		this.addTimestamps = true;
		this.compressBodyDataSize = defaultCompressBodyDataSize;
	}
	
	public Optional<Path> serverRoot() {
		synchronized ( this ) {
			return serverRoot == null ? Optional.empty() : Optional.of(serverRoot);
		}
	}
	
	public void serverRoot(Path path) {
		synchronized ( this ) {
			this.serverRoot = Objects.requireNonNull(path);
		}
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
	
	public List<String> directoryIndexes() {
		synchronized ( this ) {
			return Collections.unmodifiableList(directoryIndexes);
		}
	}
	
	public boolean directoryIndex(CharSequence cs) {
		synchronized ( this ) {
			return this.directoryIndexes.add(cs.toString());
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
	
	public long maxFileSize() {
		synchronized ( this ) {
			return maxFileSize;
		}
	}
	
	public void maxFileSize(long size) {
		
		synchronized ( this ) {
			
			if ( size <= 0L ) {
				throw new IllegalArgumentException("maxFileSize is >0");
			}
			
			this.maxFileSize = size;
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
	
	public int compressBodyDataSize() {
		synchronized ( this ) {
			return compressBodyDataSize;
		}
	}
	
	public void compressBodyDataSize(int size) {
		synchronized ( this ) {
			this.compressBodyDataSize = size;
		}
	}
	
}
