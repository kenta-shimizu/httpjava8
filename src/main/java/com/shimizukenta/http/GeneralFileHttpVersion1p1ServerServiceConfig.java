package com.shimizukenta.http;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GeneralFileHttpVersion1p1ServerServiceConfig {
	
	private Path serverRoot;
	private final List<String> directoryIndexFiles = new CopyOnWriteArrayList<>();
	private final HttpVersion1p1ResponseMessageBuilderConfig builderConfig = new HttpVersion1p1ResponseMessageBuilderConfig();
	
	public GeneralFileHttpVersion1p1ServerServiceConfig() {
		this.serverRoot = null;
	}
	
	public void serverRoot(Path path) {
		synchronized ( this ) {
			this.serverRoot = path;
		}
	}
	
	public Path serverRoot() {
		synchronized ( this ) {
			
			if ( serverRoot == null ) {
				throw new IllegalStateException("Server-Root not setted");
			}
			
			return this.serverRoot;
		}
	}
	
	public boolean addDirectoryIndexFile(CharSequence indexFile) {
		return this.directoryIndexFiles.add(indexFile.toString());
	}
	
	public boolean removeDirectoryIndexFile(CharSequence indexFile) {
		return this.directoryIndexFiles.remove(indexFile.toString());
	}
	
	public List<String> directoryIndexFiles() {
		return Collections.unmodifiableList(this.directoryIndexFiles);
	}
	
	public HttpVersion1p1ResponseMessageBuilderConfig builderConfig() {
		return this.builderConfig;
	}
	
	public void serverName(CharSequence serverName) {
		this.builderConfig.serverName(serverName);
	}
	
}
