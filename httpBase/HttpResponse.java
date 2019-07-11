package httpBase;

public class HttpResponse {
	
	private final HttpResponseHeader header;
	private final HttpResponseBody body;
	
	public HttpResponse(HttpResponseHeader header, HttpResponseBody body) {
		this.header = header;
		this.body = body;
	}
	
	public HttpResponseHeader header() {
		return this.header;
	}
	
	public byte[] body() {
		return body.value();
	}
}
