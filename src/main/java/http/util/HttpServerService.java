package http.util;

import java.nio.charset.StandardCharsets;

import http.HttpMessageParseException;
import http.HttpServerServiceSupplier;
import http.base.HttpRequestMessage;

abstract public class HttpServerService implements HttpServerServiceSupplier {

	public HttpServerService(HttpServerServiceConfig config) {
		super();
	}
	
	protected static final QueryParser queryParser = QueryParser.getInstance();
	
	protected static String getAbsolutePath(HttpRequestMessage request) throws HttpMessageParseException {
		return (request.requestLine().uri().split("\\?"))[0];
	}
	
	protected static QueryParseResult getQuery(HttpRequestMessage request) throws HttpMessageParseException {
		
		switch ( request.requestLine().method() ) {
		case HEAD:
		case GET: {
			
			String[] ss = request.requestLine().uri().split("\\?", 2);
			if ( ss.length == 2 ) {
				
				return queryParser.parse(ss[1]);
				
			} else {
				
				return queryParser.empty();
			}
			/* break; */
		}
		case POST:
		default: {
			
			return queryParser.parse(new String(request.body().getBytes(), StandardCharsets.UTF_8));
		}
		}
	}
	

}
