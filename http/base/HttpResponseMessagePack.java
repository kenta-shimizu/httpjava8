package http.base;

public class HttpResponseMessagePack extends HttpResponseMessage {
	
	private final HttpRequestMessagePack request;
	
	public HttpResponseMessagePack(
			HttpRequestMessagePack request
			, HttpStatusLine statusLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		
		super(statusLine, headerGroup, body);
		
		this.request = request;
	}
	
	public HttpResponseMessagePack(
			HttpRequestMessagePack request
			, HttpStatusLine statusLine, HttpHeaderGroup headerGroup) {
		
		this(request, statusLine, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpRequestMessagePack requestMessage() {
		return request;
	}
	
	public HttpResponseMessage responseMessage() {
		return this;
	}

}
