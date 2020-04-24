package com.shimizukenta.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HttpVersion1p1ResponseMessageBuilder extends AbstractHttpResponseMessageBuilder {
	
	private final HttpVersion1p1ResponseMessageBuilderConfig config;
	
	public HttpVersion1p1ResponseMessageBuilder(HttpVersion1p1ResponseMessageBuilderConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	protected HttpVersion version() {
		return HttpVersion.HTTP_1_1;
	}
	
	@Override
	public HttpResponseMessage fromFile(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			Path path,
			HttpContentType type) {
		
		try {
			
			if ( ! Files.exists(path) ) {
				return build(HttpStatus.NOT_FOUND);
			}
			
			byte[] body = Files.readAllBytes(path);
			
			return fromInner(request, connectionValue, body, type, path);
		}
		catch ( IOException e ) {
			return build(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public HttpResponseMessage fromJson(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			byte[] json) {
		
		return fromInner(request, connectionValue, json,
				HttpContentType.get("json")
				, null);
	}
	
	@Override
	public HttpResponseMessage fromXml(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			byte[] xml) {
		
		return fromInner(request, connectionValue, xml,
				HttpContentType.get("xml"),
				null);
	}
	
	private HttpResponseMessage fromInner(
			HttpRequestMessage request,
			HttpServerConnectionValue connectionValue,
			byte[] body,
			HttpContentType type,
			Path filePath) {
		
		if ( ! request.headerGroup()
				.getFieldValue(HttpMessageHeaderField.Host)
				.isPresent()) {
			
			/* "Host:" is not exist */
			return build(HttpStatus.BAD_REQUEST);
		}
		
		try {
			final List<HttpMessageHeader> headers = new ArrayList<>();
			
			InnerEncodingResult encResult = encodeBody(body, request);
			
			final HttpMessageBody msgBody = HttpMessageBody.fromBytes(encResult.data);
			
			if ( (long)(msgBody.length()) > config.maxFileSize() ) {
				return build(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
			}
			
			addServerHeader(headers);
			
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.ContentType,
					type.contentType()));
			
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.AcceptRanges,
					"bytes"));
			
			if ( ! encResult.encoding.isEmpty() ) {
				
				headers.add(HttpMessageHeader.create(
						HttpMessageHeaderField.ContentEncoding,
						encResult.encoding));
			}
			
			this.addDateHeader(headers);
			
			if ( filePath == null ) {
				
				addLastModifiedHeader(headers);
				
			} else {
				
				addLastModifiedHeader(headers, filePath);
			}
			
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.ContentLength,
					String.valueOf(msgBody.length())));
			
			addCORSHeaders(headers, request);
			
			addConnectionKeepAlive(headers, request, connectionValue.keepAlive());
			
			final HttpMessageHeaderGroup headerGroup = HttpMessageHeaderGroup.create(headers);
			
			HttpMethod method = request.requestLine().method();
			
			if ( method == HttpMethod.HEAD ) {
				
				return build(HttpStatus.OK, headerGroup);
				
			} else {
				
				return build(HttpStatus.OK, headerGroup, msgBody);
			}
		}
		catch ( IOException e ) {
			return build(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private class InnerEncodingResult {
		private final String encoding;
		private final byte[] data;
		private InnerEncodingResult(String enc, byte[] data) {
			this.encoding = enc;
			this.data = data;
		}
	}
	
	private static final String EncodingGZIP = "gzip";
	private static final String EncodingDeflate = "deflate";
	
	private InnerEncodingResult encodeBody(byte[] body, HttpRequestMessage request)
			throws IOException {
		
		String encValue = request.headerGroup()
				.getFieldValue(HttpMessageHeaderField.AcceptEncoding)
				.orElse("");
		
		HttpQualityParameter qp = HttpQualityParameter.parse(encValue);
		
		for ( String p : qp.parameters() ) {
			
			if ( p.equalsIgnoreCase(EncodingGZIP) ) {
				
				byte[] encBody = encodeGZIP(body);
				
				if ( encBody.length < body.length ) {
					return new InnerEncodingResult(EncodingGZIP, encBody);
				}
				
				break;
			}
			
			if ( p.equalsIgnoreCase(EncodingDeflate) ) {
				
				byte[] encBody = encodeDeflate(body);
				
				if ( encBody.length < body.length ) {
					return new InnerEncodingResult(EncodingDeflate, encBody);
				}
				
				break;
			}
		}
		
		return new InnerEncodingResult("", body);
	}
	
	protected void addConnectionKeepAlive(
			List<HttpMessageHeader> headers,
			HttpRequestMessage request,
			HttpKeepAliveValue keepAliveValue
			) {
		
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
	}
	
	protected void addCORSHeaders(
			List<HttpMessageHeader> headers,
			HttpRequestMessage request) {
		
		String origin = request.headerGroup()
				.getFieldValue(HttpMessageHeaderField.Origin)
				.orElse("");
		
		if ( ! origin.isEmpty() ) {
			
			if ( config.acceptControlAllowOrigin() ) {
				
				headers.add(HttpMessageHeader.create(
						HttpMessageHeaderField.AccessControlAllowOrigin,
						origin));
				
				if ( config.accessControlAllowCredentials() ) {
					
					headers.add(HttpMessageHeader.create(
							HttpMessageHeaderField.AccessControlAllowCredentials,
							"true"));
				}
			}
		}
	}
	
}
