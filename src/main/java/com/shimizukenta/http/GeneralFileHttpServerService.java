package com.shimizukenta.http;

import java.io.IOException;
import java.nio.file.Path;

public class GeneralFileHttpServerService extends AbstractHttpServerService {
	
	private final GeneralFileHttpServerServiceConfig config;
	
	public GeneralFileHttpServerService(GeneralFileHttpServerServiceConfig config) {
		super();
		this.config = config;
	}

	@Override
	public boolean accept(HttpRequestMessageInformation requestInfo) {
		return true;
	}

	@Override
	public boolean tryService(
			HttpMessageWriter writer,
			HttpRequestMessageInformation requestInfo,
			HttpServerConnectionValue connectionValue)
					throws InterruptedException, HttpWriteException {
		
		try {
			HttpResponseMessage rsp = response(requestInfo, connectionValue);
			write(writer, rsp, requestInfo);
			return rsp.isKeepAlive();
		}
		catch ( IOException e ) {
			this.putLog(new HttpLog(e));
		}
		
		return false;
	}
	
	private HttpResponseMessage response(
			HttpRequestMessageInformation requestInfo,
			HttpServerConnectionValue connectionValue)
					throws IOException {
		
		Path filePath = getFilePath(requestInfo.path());
		
		
		//TODO
		
		return null;
	}
	
	private Path getFilePath(String path) {
		
		//TODO
		
		return null;
	}

}
