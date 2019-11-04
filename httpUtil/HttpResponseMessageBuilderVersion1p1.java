package httpUtil;

import java.util.ArrayList;
import java.util.List;

import httpBase.HttpContentType;
import httpBase.HttpHeader;
import httpBase.HttpHeaderField;
import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpResponseMessage;
import httpBase.HttpVersion;

public class HttpResponseMessageBuilderVersion1p1 extends HttpResponseMessageBuilder {

	public HttpResponseMessageBuilderVersion1p1() {
		super();
	}

	@Override
	protected HttpVersion getHttpVersion() {
		return HttpVersion.HTTP1_1;
	}

	@Override
	public HttpResponseMessage createOkResponse(HttpMessageBody body) {
		return createOkResponse(createHeaderGroup(body), body);
	}

	@Override
	public HttpResponseMessage createOkHeadResponse(HttpMessageBody body) {
		return createOkHeadResponse(createHeaderGroup(body));
	}
	
	private HttpHeaderGroup createHeaderGroup(HttpMessageBody body) {
		
		List<HttpHeader> headers = new ArrayList<>();
		
		body.contentType()
		.map(HttpContentType::contentType)
		.map(type -> new HttpHeader(HttpHeaderField.ContentType, type))
		.ifPresent(headers::add);
		
		int size = body.getBytes().length;
		
		if ( size > 0 ) {
			headers.add(new HttpHeader(HttpHeaderField.ContentLength, String.valueOf(size)));
		}
		
		return HttpHeaderGroup.create(headers);
	}
	
}
