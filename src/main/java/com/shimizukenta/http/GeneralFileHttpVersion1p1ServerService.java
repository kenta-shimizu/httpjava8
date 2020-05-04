package com.shimizukenta.http;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GeneralFileHttpVersion1p1ServerService extends AbstractHttpServerService {
	
	private final GeneralFileHttpVersion1p1ServerServiceConfig config;
	private final HttpVersion1p1ResponseMessageBuilder msgBuilder;
	
	public GeneralFileHttpVersion1p1ServerService(GeneralFileHttpVersion1p1ServerServiceConfig config) {
		super();
		this.config = config;
		this.msgBuilder = new HttpVersion1p1ResponseMessageBuilder(config.builderConfig());
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
		
		HttpResponseMessage rsp = response(requestInfo, connectionValue);
		write(writer, rsp, requestInfo);
		return rsp.isKeepAlive();
	}
	
	private HttpResponseMessage response(
			HttpRequestMessageInformation requestInfo,
			HttpServerConnectionValue connectionValue) {
		
		try {
			
			switch ( requestInfo.version() ) {
			case HTTP_1_0:
			case HTTP_1_1: {
				
				/* through */
				break;
			}
			case HTTP_2_0:
			default: {
				
				return msgBuilder.build(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
			}
			}
			
			Path filePath = getFilePath(requestInfo.path());
			
			if ( filePath != null && Files.exists(filePath) && ! Files.isDirectory(filePath) ) {
				
				if ( Files.isReadable(filePath) ) {
					
					switch ( requestInfo.method() ) {
					case HEAD:
					case GET: {
						
						return msgBuilder.fromFile(requestInfo.requestMessage(), connectionValue, filePath);
						/* break; */
					}
					case OPTIONS: {
						
						return responsePreflight(requestInfo.requestMessage(), connectionValue.keepAlive());
						/* break; */
					}
					default: {
						
						return msgBuilder.build(HttpStatus.NOT_ACCEPTABLE);
					}
					}
					
				} else {
					
					return msgBuilder.build(HttpStatus.FORBIDDEN);
				}
				
			} else {
				
				return msgBuilder.build(HttpStatus.NOT_FOUND);
			}
		}
		catch ( Throwable t ) {
			putLog(t);
			return msgBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Path getFilePath(String path) {
		
		if ( path.endsWith("/") ) {
			
			Path dirPath = config.serverRoot();
			
			{
				String pp = path.substring(0, (path.length() - 1));
				
				if ( ! pp.isEmpty() ) {
					dirPath = dirPath.resolve(pp.substring(1));
				}
			}
			
			for ( String indexFile : config.directoryIndexFiles() ) {
				
				Path filePath = dirPath.resolve(indexFile);
				
				if ( Files.exists(filePath) && ! Files.isDirectory(filePath) ) {
					return filePath;
				}
			}
			
			return null;
			
		} else {
			
			return config.serverRoot().resolve(path.substring(1));
		}
	}
	
	private HttpResponseMessage responsePreflight(
			HttpRequestMessage request,
			HttpKeepAliveValue keepAliveValue) {
		
		if ( config.builderConfig().acceptControlAllowOrigin() ) {
			
			final String origin = request.headerGroup()
					.getFieldValue(HttpMessageHeaderField.Origin)
					.filter(v -> ! v.isEmpty())
					.orElse("");
			
			if ( ! origin.isEmpty() ) {
				
				final List<HttpMessageHeader> headers = new ArrayList<>();
				
				headers.add(HttpMessageHeader.create(
						HttpMessageHeaderField.AccessControlAllowOrigin,
						origin));
				
				if ( config.builderConfig().accessControlAllowCredentials() ) {
					
					headers.add(HttpMessageHeader.create(
							HttpMessageHeaderField.AccessControlAllowCredentials,
							"true"));
				}
				
				headers.add(HttpMessageHeader.create(
						HttpMessageHeaderField.AccessControlAllowMethods,
						"GET,HEAD,OPTIONS"));
				
				String connection = request.headerGroup()
						.getFieldValue(HttpMessageHeaderField.AcceptEncoding)
						.orElse("keep-alive");
				
				if ( connection.equalsIgnoreCase("close") ) {
					
					headers.add(HttpMessageHeader.connectionClose());
					
				} else {
					
					int rem = keepAliveValue.decreaseRemaining();
					
					if ( rem > 0 ) {
						
						headers.add(HttpMessageHeader.connectionKeepAlive());
						
						headers.add(HttpMessageHeader.create(
								HttpMessageHeaderField.KeepAlive,
								("timeout=" + keepAliveValue.timeout() + ", max=" + rem)));
						
					} else {
						
						headers.add(HttpMessageHeader.connectionClose());
					}
				}
				
				return msgBuilder.build(
						HttpStatus.NO_CONTENT,
						HttpMessageHeaderGroup.create(headers));
			}
		}
		
		return msgBuilder.build(HttpStatus.NOT_ACCEPTABLE);
		
	}
	
}
