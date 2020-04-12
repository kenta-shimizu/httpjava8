package example2;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shimizukenta.http.HttpContentType;
import com.shimizukenta.http.HttpHeader;
import com.shimizukenta.http.HttpHeaderField;
import com.shimizukenta.http.HttpMethod;
import com.shimizukenta.http.HttpStatus;
import com.shimizukenta.http.HttpVersion;
import com.shimizukenta.http.QueryParseResult;

import http.HttpMessageParseException;
import http.HttpWriteMessageException;
import http.api.HttpApiServerService;
import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpMessageWriter;
import http.base.HttpRequestMessage;
import http.base.HttpResponseMessage;
import http.base.HttpServerConnectionValue;
import http.util.HttpResponseMessageBuilder;
import http.util.HttpResponseMessageBuilders;

public class ExampleHttpApiServerService extends HttpApiServerService {
	
	private static final DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	private final HttpResponseMessageBuilder msgBuilder = HttpResponseMessageBuilders.get(HttpVersion.HTTP1_1);
	
	private final ExampleHttpApiServerServiceConfig config;
	
	public ExampleHttpApiServerService(ExampleHttpApiServerServiceConfig config) {
		super(config);
		
		this.config = config;
	}

	@Override
	public boolean tryService(
			HttpMessageWriter writer
			, HttpRequestMessage request
			, HttpServerConnectionValue connectionValue)
					throws InterruptedException
					, HttpWriteMessageException
					, HttpMessageParseException {
		
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
				HttpResponseMessage rspMsg = createResponseMessage(request, connectionValue);
				
				writer.write(rspMsg);
				
				return rspMsg.isKeepAlive();
			}
			catch ( HttpMessageParseException e ) {
				writer.write(msgBuilder.build(HttpStatus.BAD_REQUEST));
				throw e;
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
	
	private HttpResponseMessage createResponseMessage(
			HttpRequestMessage request
			, HttpServerConnectionValue connectionValue)
					throws HttpMessageParseException {
		
		final HttpMethod method = request.requestLine().method();
		
		final List<HttpHeader> headers = new ArrayList<>();
		
		config.serverName()
		.map(s -> new HttpHeader(HttpHeaderField.Server, s))
		.ifPresent(headers::add);
		
		headers.add(new HttpHeader(HttpHeaderField.AcceptRanges, "bytes"));
		
		if ( config.addTimestamps() ) {
			
			ZonedDateTime date = ZonedDateTime.now(ZoneId.of("UTC"));
			headers.add(new HttpHeader(HttpHeaderField.Date, date.format(rfc1123Formatter)));
			headers.add(new HttpHeader(HttpHeaderField.LastModified, date.format(rfc1123Formatter)));
		}
		
		headers.addAll(createKeepAliveHeaders(request, connectionValue));
		
		headers.add(new HttpHeader(HttpHeaderField.ContentType, HttpContentType.get("html").contentType()));
		
		return createIdentifyResponseMessage(createText(request), headers, method);
	}
	
	private String createText(HttpRequestMessage request) throws HttpMessageParseException {
		
		QueryParseResult query = getQuery(request);
		
		return "<html><head><title>Example-API</title></head><body><p>query is <br />"
		+ query.toString()
		+ "</p></body></html>";
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
	
	protected HttpResponseMessage createIdentifyResponseMessage(CharSequence text, List<HttpHeader> headers, HttpMethod method) {
		
		List<HttpHeader> hh = new ArrayList<>(headers);
		
		byte[] body = text.toString().getBytes(StandardCharsets.UTF_8);
		
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
