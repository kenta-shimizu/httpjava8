package http.base;

import java.util.Optional;

import com.shimizukenta.http.HttpContentType;

public class HttpMessageBody {
	
	private byte[] body;
	private HttpContentType contentType;
	
	public HttpMessageBody(byte[] body) {
		this.body = body;
		this.contentType = null;
	}
	
	public byte[] getBytes() {
		return body;
	}
	
	public void contentType(HttpContentType contentType) {
		this.contentType = contentType;
	}
	
	public Optional<HttpContentType> contentType() {
		return contentType == null ? Optional.empty() : Optional.of(contentType);
	}
	
	public String toString() {
		
		int len = body.length;
		
		if ( len == 0 ) {
			
			return "";
			
		} else {	
			
			return "body-length: " + body.length;
		}
	}
	
	private static class SingletonHolder {
		private static final HttpMessageBody empty = new HttpMessageBody(new byte[0]);
	}
	
	public static HttpMessageBody empty() {
		return SingletonHolder.empty;
	}
}
