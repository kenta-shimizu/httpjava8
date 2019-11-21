package http.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import http.base.HttpContentType;
import http.base.HttpHeader;
import http.base.HttpHeaderField;
import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpMessageParseException;
import http.base.HttpMessageWriter;
import http.base.HttpMethod;
import http.base.HttpRequestMessage;
import http.base.HttpResponseMessage;
import http.base.HttpServerConnectionValue;
import http.base.HttpStatus;
import http.base.HttpVersion;
import http.base.HttpWriteMessageException;

public class HttpGeneralFileServerService extends HttpServerService {
	
	private final DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	private final HttpGeneralFileServerServiceConfig config;
	private final HttpResponseMessageBuilder msgBuilder = HttpResponseMessageBuilders.get(HttpVersion.HTTP1_1);
	
	public HttpGeneralFileServerService(HttpGeneralFileServerServiceConfig config) {
		super(config);
		
		this.config = config;
	}
	
	@Override
	public boolean accept(HttpRequestMessage request) {
		return true;
	}
	
	@Override
	public boolean tryService(HttpMessageWriter writer, HttpRequestMessage request, HttpServerConnectionValue connectionValue)
			throws InterruptedException, HttpWriteMessageException, HttpMessageParseException {
		
		switch (request.requestLine().version()) {
		case HTTP1_0:
		case HTTP1_1: {
			
			/* throw */
			break;
		}
		case HTTP2_0:
		default: {
			
			writer.write(msgBuilder.build(HttpStatus.HTTP_VERSION_NOT_SUPPORTED));
			return false;
		}
		}
		
		switch (request.requestLine().method()) {
		case HEAD:
		case GET: {
			
			try {
				Path path = getFileAbsPath(request);
				
				if ( path == null ) {
					writer.write(msgBuilder.build(HttpStatus.NOT_FOUND));
					return false;
				}
				
				if ( Files.size(path) > config.maxFileSize() ) {
					writer.write(msgBuilder.build(HttpStatus.REQUEST_ENTITY_TOO_LARGE));
					return false;
				}
				
				HttpResponseMessage rspMsg = createResponseMessage(path, request, connectionValue);
				
				writer.write(rspMsg);
				
				return rspMsg.isKeepAlive();
			}
			catch ( HttpMessageParseException e ) {
				writer.write(msgBuilder.build(HttpStatus.BAD_REQUEST));
				throw e;
			}
			catch ( IOException e ) {
				writer.write(msgBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR));
				throw new HttpWriteMessageException(e);
			}
			
			/* break; */
		}
		case POST:
		default: {
			
			writer.write(msgBuilder.build(HttpStatus.METHOD_NOT_ALLOWED));
			return false;
		}
		}
	}
	
	protected Path getFileAbsPath(HttpRequestMessage request) throws HttpMessageParseException {
		
		final String absPath = getAbsolutePath(request);
		
		if ( absPath.length() < 1 ) {
			throw new HttpMessageParseException("AbsolutePath length < 1");
		}
		
		Path path = config.serverRoot()
				.map(p -> {
					
					String s = absPath.substring(1);
					
					if ( s.isEmpty() ) {
						return p;
					}
					
					return p.resolve(s);
				})
				.orElseThrow(() -> new HttpMessageParseException("Server Root not setted"));
		
		if (Files.isDirectory(path)) {
			
			for ( String indexFileName : config.directoryIndexes() ) {
				
				Path pp = path.resolve(indexFileName);
				
				if ( Files.isReadable(pp) ) {
					return pp;
				}
			}
			
			return null;
			
		} else {
			
			return Files.isReadable(path) ? path : null;
		}
	}
	
	private HttpResponseMessage createResponseMessage(
			Path path
			, HttpRequestMessage request
			, HttpServerConnectionValue connectionValue)
					throws IOException, HttpMessageParseException {
		
		if ( Files.size(path) > config.maxFileSize() ) {
			return msgBuilder.build(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
		}
		
		final HttpMethod method = request.requestLine().method();
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		config.serverName()
		.map(s -> new HttpHeader(HttpHeaderField.Server, s))
		.ifPresent(headers::add);
		
		headers.add(new HttpHeader(HttpHeaderField.AcceptRanges, "bytes"));
		
		if ( config.addTimestamps() ) {
			
			LocalDateTime date = LocalDateTime.now();
			headers.add(new HttpHeader(HttpHeaderField.Date, rfc1123Formatter.format(date)));
			
			FileTime ft = Files.getLastModifiedTime(path);
			LocalDateTime lmt = LocalDateTime.ofInstant(ft.toInstant(), ZoneId.systemDefault());
			headers.add(new HttpHeader(HttpHeaderField.LastModified, rfc1123Formatter.format(lmt)));
		}
		
		headers.addAll(createKeepAliveHeaders(request, connectionValue));
		
		{
			String[] exts = path.getFileName().toString().split("\\.");
			String ext = exts[exts.length - 1];
			
			headers.add(new HttpHeader(
					HttpHeaderField.ContentType
					, HttpContentType.get(ext).contentType()));
		}
		
		byte[] bs = Files.readAllBytes(path);
		
		if ( bs.length > config.compressBodyDataSize() ) {
			
			for ( String enc : request.acceptEncodings() ) {
				
				if ( enc.equalsIgnoreCase("gzip") ) {
					return createGzipResponseMessage(bs, headers, method);
				}
				
				if ( enc.equalsIgnoreCase("deflate") ) {
					return createDeflateResponseMessage(bs, headers, method);
				}
			}
		}
		
		return createIdentityResponseMessage(bs, headers, method);
	}
	
	private List<HttpHeader> createKeepAliveHeaders(HttpRequestMessage request, HttpServerConnectionValue connectionValue) {
		
		if ( request.isKeepAlive() ) {
			
			if ( connectionValue.keepAlive().remaining() > 0 ) {
				
				String v = "timeout="
						+ connectionValue.keepAlive().timeout()
						+ ", max="
						+ connectionValue.keepAlive().decreaseRemaining();
				
				List<HttpHeader> ll = Arrays.asList(
						new HttpHeader(HttpHeaderField.KeepAlive, v)
						, new HttpHeader(HttpHeaderField.Connection, "Keep-Alive")
						);
				
				connectionValue.keepAlive().decreaseRemaining();
				
				return ll;
			}
		}
		
		return Arrays.asList(new HttpHeader(HttpHeaderField.Connection, "Close"));
	}
	
	protected HttpResponseMessage createIdentityResponseMessage(byte[] body, List<HttpHeader> headers, HttpMethod method) {
		return createBaseResponseMessage(body, headers, method);
	}
	
	protected HttpResponseMessage createGzipResponseMessage(byte[] body, List<HttpHeader> headers, HttpMethod method) throws IOException {
		
		List<HttpHeader> hh = new ArrayList<>(headers);
		
		hh.add(new HttpHeader(HttpHeaderField.ContentEncoding, "gzip"));
		
		try (
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				) {
			
			try (
					GZIPOutputStream gzos = new GZIPOutputStream(baos);
					) {
				
				gzos.write(body);
			}
			
			return createBaseResponseMessage(baos.toByteArray(), hh, method);
		}
	}
	
	protected HttpResponseMessage createDeflateResponseMessage(byte[] body, List<HttpHeader> headers, HttpMethod method) {
		
		List<HttpHeader> hh = new ArrayList<>(headers);
		
		hh.add(new HttpHeader(HttpHeaderField.ContentEncoding, "deflate"));
		
		//TODO
		//HOOK
		//zlib
		
		return createIdentityResponseMessage(body, headers, method);
	}
	
	protected HttpResponseMessage createBaseResponseMessage(byte[] body, List<HttpHeader> headers, HttpMethod method) {
		
		List<HttpHeader> hh = new ArrayList<>(headers);
		
		hh.add(new HttpHeader(HttpHeaderField.ContentLength, String.valueOf(body.length)));
		
		switch ( method ) {
		case HEAD : {
			
			return msgBuilder.build(
					HttpStatus.OK
					, HttpHeaderGroup.create(hh));
			/* break; */
		}
		case GET : {
			
			return msgBuilder.build(
					HttpStatus.OK
					, HttpHeaderGroup.create(hh)
					, new HttpMessageBody(body));
			/* break; */
		}
		default:
			
			return msgBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
