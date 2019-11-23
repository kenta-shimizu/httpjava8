package http.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HttpGeneralFileServerServiceConfig extends HttpServerServiceConfig {
	
	private static final int defaultCompressBodyDataSize = 256 * 16;
	
	private Path serverRoot;
	private final List<String> directoryIndexes = new ArrayList<>();
	private long maxFileSize;
	private int compressBodyDataSize;
	
	public HttpGeneralFileServerServiceConfig() {
		super();
		
		this.serverRoot = null;
		this.maxFileSize = (long)(Integer.MAX_VALUE);
		this.compressBodyDataSize = defaultCompressBodyDataSize;
	}
	
	public Path serverRoot() {
		synchronized ( this ) {
			
			if ( serverRoot == null ) {
				throw new IllegalStateException("Server Root not setted");
			}
			
			return serverRoot;
		}
	}
	
	public void serverRoot(Path path) {
		synchronized ( this ) {
			this.serverRoot = Objects.requireNonNull(path);
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
