package http.base;

import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageStatusLine;

public class HttpResponseMessagePack extends HttpResponseMessage {
	
	private final HttpRequestMessagePack request;
	
	public HttpResponseMessagePack(
			HttpRequestMessagePack request
			, HttpMessageStatusLine statusLine, HttpMessageHeaderGroup headerGroup, HttpMessageBody body) {
		
		super(statusLine, headerGroup, body);
		
		this.request = request;
	}
	
	public HttpResponseMessagePack(
			HttpRequestMessagePack request
			, HttpMessageStatusLine statusLine, HttpMessageHeaderGroup headerGroup) {
		
		this(request, statusLine, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpResponseMessagePack(
			HttpRequestMessagePack request
			, HttpResponseMessage response) {
		
		this(request, response.statusLine(), response.headerGroup(), response.body());
	}
	
	public HttpRequestMessagePack requestMessage() {
		return request;
	}
	
	public HttpResponseMessage responseMessage() {
		return this;
	}

}
