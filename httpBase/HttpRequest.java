package httpBase;

public class HttpRequest {
	
	private final HttpRequestHeader header;
	private final HttpRequestBody body;

	public HttpRequest(HttpRequestHeader header, HttpRequestBody body) {
		this.header = header;
		this.body = body;
	}
	
	public HttpRequestHeader header() {
		return this.header;
	}
	
	public byte[] body() {
		return this.body.value();
	}

}
