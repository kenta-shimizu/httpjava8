package http.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpMessageTextBody extends HttpMessageBody {
	
	private final String text;
	
	public HttpMessageTextBody(CharSequence text, Charset charset) {
		super(text.toString().getBytes(charset));
		this.text = text.toString();
	}
	
	public HttpMessageTextBody(CharSequence text) {
		this(text, StandardCharsets.UTF_8);
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	private static class SingletonHolder {
		private static final HttpMessageTextBody empty = new HttpMessageTextBody("");
	}
	
	public static HttpMessageTextBody empty() {
		return SingletonHolder.empty;
	}
	
}
