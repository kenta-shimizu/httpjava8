package httpBase;

import java.util.Arrays;

public class HttpResponseBody {
	
	private final byte[] bodyBytes;
	
	public HttpResponseBody(byte[] bs) {
		bodyBytes = Arrays.copyOf(bs, bs.length);
	}
	
	public byte[] value() {
		return Arrays.copyOf(bodyBytes, bodyBytes.length);
	}
	
	public static HttpResponseBody empty() {
		return new HttpResponseBody(new byte[0]);
	}
	
}
