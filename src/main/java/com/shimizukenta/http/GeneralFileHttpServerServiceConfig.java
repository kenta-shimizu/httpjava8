package com.shimizukenta.http;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralFileHttpServerServiceConfig {
	
	private Path serverRoot;
	private final List<String> directoryIndexFiles = new ArrayList<>();
	
	public GeneralFileHttpServerServiceConfig() {
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
				
				//TODO
				throw new RuntimeException();
			}
			
			return this.serverRoot;
		}
	}
	
	public boolean addDirectoryIndexFile(CharSequence indexFile) {
		synchronized ( this ) {
			return this.directoryIndexFiles.add(indexFile.toString());
		}
	}
	
	public boolean removeDirectoryIndexFile(CharSequence indexFile) {
		synchronized ( this ) {
			return this.directoryIndexFiles.remove(indexFile.toString());
		}
	}
	
	public List<String> directoryIndexFiles() {
		synchronized ( this ) {
			return Collections.unmodifiableList(this.directoryIndexFiles);
		}
	}
	
	//TODO
	//serverName
	
}
