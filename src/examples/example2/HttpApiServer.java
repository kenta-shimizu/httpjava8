package example2;

import java.io.IOException;

import http.base.HttpMessageParseException;
import http.base.HttpMessageWriter;
import http.base.HttpRequestMessage;
import http.base.HttpServerConnectionValue;
import http.base.HttpWriteMessageException;
import http.util.HttpServer;

public class HttpApiServer extends HttpServer {

	private final HttpApiServerService apiService;
	
	public HttpApiServer(HttpApiServerConfig config) {
		super(config);
		
		this.apiService = new HttpApiServerService(config.apiServerServiceConfig());
	}
	
	public static HttpApiServer open(HttpApiServerConfig config) throws IOException {
		
		HttpApiServer inst = new HttpApiServer(config);
		
		try {
			inst.open();
		}
		catch ( IOException e ) {
			
			try {
				inst.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		
		return inst;
	}
	
	@Override
	protected boolean writeResponseMessage(
			HttpMessageWriter writer
			, HttpRequestMessage request
			, HttpServerConnectionValue connectionValue)
					throws InterruptedException
					, HttpWriteMessageException
					, HttpMessageParseException {
		
		if ( apiService.accept(request) ) {
			return apiService.tryService(writer, request, connectionValue);
		}
		
		return super.writeResponseMessage(writer, request, connectionValue);
	}
	
}
