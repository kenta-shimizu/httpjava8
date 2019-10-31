package httpBase;

public class HttpMessageBody {
	
	private byte[] body;
	
	public HttpMessageBody(byte[] body) {
		this.body = body;
	}
	
	public byte[] getBytes() {
		return body;
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
