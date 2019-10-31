package util;

import java.util.List;
import java.util.stream.Collectors;

import httpBase.HttpRequest;
import httpBase.HttpRequestBody;
import httpBase.HttpRequestHeader;
import httpBase.HttpVersion;

public class HttpRequestBuilderVersion1p1 extends HttpRequestBuilder {
	
	private static final String version = HttpVersion.VER1_1.toString();
	
	protected HttpRequestBuilderVersion1p1() {
		super();
	}

	@Override
	public HttpRequest create(CharSequence method, CharSequence path, CharSequence host
			, List<? extends CharSequence> fields
			, byte[] body) {
		
		final List<String> newFields = fields.stream()
				.map(f -> f.toString())
				.collect(Collectors.toList());
		
		newFields.add(createHostField(host));
		
		int contentLength = body.length;
		
		if ( contentLength > 0 ) {
			newFields.add(createContentLengthField(contentLength));
		}
		
		return new HttpRequest(
				new HttpRequestHeader(method, path, version, newFields)
				, new HttpRequestBody(body));
	}
	
}
