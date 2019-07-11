package httpBase;

import java.util.Arrays;

public class HttpRequestBody {
	
	public final byte[] bodyBytes;
	
	public HttpRequestBody(byte[] bs) {
		this.bodyBytes = Arrays.copyOf(bs, bs.length);
	}
	
	public byte[] value() {
		return Arrays.copyOf(bodyBytes, bodyBytes.length);
	}
	
	public static HttpRequestBody empty() {
		return new HttpRequestBody(new byte[0]);
	}

}
