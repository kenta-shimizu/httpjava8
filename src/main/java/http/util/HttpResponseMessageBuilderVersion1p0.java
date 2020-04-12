package http.util;

import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.http.HttpContentType;
import com.shimizukenta.http.HttpHeader;
import com.shimizukenta.http.HttpHeaderField;
import com.shimizukenta.http.HttpVersion;

import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpResponseMessage;

public class HttpResponseMessageBuilderVersion1p0 extends HttpResponseMessageBuilder {

	public HttpResponseMessageBuilderVersion1p0() {
		super();
	}

	@Override
	protected HttpVersion getHttpVersion() {
		return HttpVersion.HTTP1_0;
	}

	@Override
	public HttpResponseMessage buildOkResponse(HttpMessageBody body) {
		return buildOkResponse(createHeaderGroup(body), body);
	}

	@Override
	public HttpResponseMessage buildOkHeadResponse(HttpMessageBody body) {
		return buildOkHeadResponse(createHeaderGroup(body));
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
