package util;

import java.util.List;

import httpBase.HttpResponse;
import httpBase.HttpStatus;

public abstract class HttpResponseBuilder {

	public HttpResponseBuilder() {
		/* Nothing */
	}
	
	public abstract HttpResponse create(
			HttpStatus status
			, List<? extends CharSequence> fields
			, byte[] body);
	
	public abstract HttpResponse createHeadResponse(
			HttpStatus status
			, List<? extends CharSequence> fields
			, byte[] body);
	
}
