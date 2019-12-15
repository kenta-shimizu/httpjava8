package http.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import http.HttpHeaderField;
import http.HttpMessageParseException;
import http.HttpMethod;
import http.HttpStatus;
import http.HttpVersion;
import http.HttpWriteMessageException;
import http.base.HttpContentType;
import http.base.HttpHeader;
import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpMessageWriter;
import http.base.HttpRequestMessage;
import http.base.HttpResponseMessage;
import http.base.HttpServerConnectionValue;

public class HttpGeneralFileServerService extends HttpServerService {
	
	private static final int ByteArrayOutputStreamSize = 256 * 64;
	
	private static final DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
	
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
		
		if ( absPath.indexOf("..") >= 0 ) {
			throw new HttpMessageParseException("AbsolutePath include \"..\"");
		}
		
		Path path;
		{
			Path serverRoot = config.serverRoot();
			String s = absPath.substring(1);
			
			if ( s.isEmpty() ) {
				
				path = serverRoot;
				
			} else {
				
				path = serverRoot.resolve(s);
			}
		}
		
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
			
			ZonedDateTime date = ZonedDateTime.now(ZoneId.of("UTC"));
			headers.add(new HttpHeader(HttpHeaderField.Date, date.format(rfc1123Formatter)));
			
			FileTime ft = Files.getLastModifiedTime(path);
			ZonedDateTime lmt = ZonedDateTime.ofInstant(ft.toInstant(), ZoneId.of("UTC"));
			headers.add(new HttpHeader(HttpHeaderField.LastModified, lmt.format(rfc1123Formatter)));
		}
		
		headers.addAll(createKeepAliveHeaders(request, connectionValue));
		
		headers.add(new HttpHeader(
				HttpHeaderField.ContentType
				, HttpContentType.get(path).contentType()));
		
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
						+ connectionValue.keepAlive().remaining();
				
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
				ByteArrayOutputStream baos = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
				) {
			
			try (
					GZIPOutputStream gzos = new GZIPOutputStream(baos);
					) {
				
				gzos.write(body);
			}
			
			return createBaseResponseMessage(baos.toByteArray(), hh, method);
		}
	}
	
	protected HttpResponseMessage createDeflateResponseMessage(byte[] body, List<HttpHeader> headers, HttpMethod method) throws IOException {
		
		List<HttpHeader> hh = new ArrayList<>(headers);
		
		hh.add(new HttpHeader(HttpHeaderField.ContentEncoding, "deflate"));
		
		final Deflater comp = new Deflater();
		
		try {
			
			comp.setInput(body);
			comp.finish();
			
			byte[] bs = new byte[4096];
			
			try (
					ByteArrayOutputStream baos = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
					) {
				
				for ( ;; ) {
					
					int len = comp.deflate(bs);
					
					if ( len > 0 ) {
						
						baos.write(bs, 0, len);
						
					} else {
						
						break;
					}
				}
				
				return createBaseResponseMessage(baos.toByteArray(), hh, method);
			}
		}
		finally {
			comp.end();
		}
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
