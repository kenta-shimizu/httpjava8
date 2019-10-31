package util;

import java.util.Collections;
import java.util.List;

import httpBase.HttpRequest;
import httpBase.HttpMethod;

public abstract class HttpRequestBuilder {
	
	public HttpRequestBuilder() {
		/* Nothing */
	}
	
	public abstract HttpRequest create(
			CharSequence method
			, CharSequence path
			, CharSequence host
			, List<? extends CharSequence> fields
			, byte[] body);
	
	public HttpRequest createHead(CharSequence path, CharSequence host) {
		return createHead(path, host, Collections.emptyList());
	}
	
	public HttpRequest createHead(CharSequence path, CharSequence host
			, List<? extends CharSequence> fields) {
		
		return create(HttpMethod.HEAD.toString(), path, host, fields, emptyBody);
	}
	
	public HttpRequest createGet(CharSequence path, CharSequence host) {
		return createGet(path, host, Collections.emptyList());
	}
	
	public HttpRequest createGet(CharSequence path, CharSequence host
			, List<? extends CharSequence> fields) {
		
		return create(HttpMethod.GET.toString(), path, host, fields,  emptyBody);
	}
	
	public HttpRequest createPost(CharSequence path, CharSequence host
			, byte[] body) {
		
		return createPost(path, host, Collections.emptyList(), body);
	}
	
	public HttpRequest createPost(CharSequence path, CharSequence host
			, List<? extends CharSequence> fields
			, byte[] body) {
		
		return create(HttpMethod.POST.toString(), path, host, fields, body);
	}
	
	public HttpRequest create(CharSequence method, CharSequence path, CharSequence host, byte[] body) {
		return create(method, path, host, Collections.emptyList(), body);
	}
	
	private static byte[] emptyBody = new byte[0];
	
	protected static byte[] emptyBody() {
		return emptyBody;
	}
	
	protected static String createHostField(CharSequence host) {
		return "Host: " + host.toString();
	}
	
	protected static String createContentLengthField(int length) {
		return "Content-Length: " + length;
	}
	
}
