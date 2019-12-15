package http.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HttpGeneralFileServerServiceConfig extends HttpServerServiceConfig {
	
	public static final int defaultCompressBodyDataSize = 256 * 16;
	public static final long defaultMaxFileSize = (long)(Integer.MAX_VALUE);
	
	private Path _serverRoot;
	private final List<String> _directoryIndexes = new ArrayList<>();
	private long _maxFileSize;
	private int _compressBodyDataSize;
	
	public HttpGeneralFileServerServiceConfig() {
		super();
		
		this._serverRoot = null;
		this._maxFileSize = defaultMaxFileSize;
		this._compressBodyDataSize = defaultCompressBodyDataSize;
	}
	
	public Path serverRoot() {
		synchronized ( this ) {
			
			if ( _serverRoot == null ) {
				throw new IllegalStateException("Server Root not setted");
			}
			
			return _serverRoot;
		}
	}
	
	public void serverRoot(Path path) {
		synchronized ( this ) {
			this._serverRoot = Objects.requireNonNull(path);
		}
	}
	
	public List<String> directoryIndexes() {
		synchronized ( this ) {
			return Collections.unmodifiableList(_directoryIndexes);
		}
	}
	
	public boolean directoryIndex(CharSequence cs) {
		synchronized ( this ) {
			return this._directoryIndexes.add(cs.toString());
		}
	}
	
	public long maxFileSize() {
		synchronized ( this ) {
			return _maxFileSize;
		}
	}
	
	public void maxFileSize(long size) {
		
		synchronized ( this ) {
			
			if ( size <= 0L ) {
				throw new IllegalArgumentException("maxFileSize is >0");
			}
			
			this._maxFileSize = size;
		}
	}
	
	public int compressBodyDataSize() {
		synchronized ( this ) {
			return _compressBodyDataSize;
		}
	}
	
	public void compressBodyDataSize(int size) {
		synchronized ( this ) {
			this._compressBodyDataSize = size;
		}
	}
	
}
