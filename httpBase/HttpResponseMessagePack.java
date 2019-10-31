package httpBase;

public class HttpResponseMessagePack extends HttpResponseMessage {
	
	private final HttpRequestMessage request;
	
	public HttpResponseMessagePack(
			HttpRequestMessage request
			, HttpStatusLine statusLine, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		
		super(statusLine, headerGroup, body);
		
		this.request = request;
	}
	
	public HttpRequestMessage requestMessage() {
		return request;
	}
	
	public HttpResponseMessage responseMessage() {
		return this;
	}

}
