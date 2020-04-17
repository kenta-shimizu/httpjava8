package http.util;

import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.httpserver.HttpContentType;
import com.shimizukenta.httpserver.HttpMessageHeader;
import com.shimizukenta.httpserver.HttpMessageHeaderField;
import com.shimizukenta.httpserver.HttpMessageHeaderGroup;
import com.shimizukenta.httpserver.HttpVersion;

import http.base.HttpMessageBody;
import http.base.HttpResponseMessage;

public class HttpResponseMessageBuilderVersion1p1 extends HttpResponseMessageBuilder {

	public HttpResponseMessageBuilderVersion1p1() {
		super();
	}

	@Override
	protected HttpVersion getHttpVersion() {
		return HttpVersion.HTTP1_1;
	}

	@Override
	public HttpResponseMessage buildOkResponse(HttpMessageBody body) {
		return buildOkResponse(createHeaderGroup(body), body);
	}

	@Override
	public HttpResponseMessage buildOkHeadResponse(HttpMessageBody body) {
		return buildOkHeadResponse(createHeaderGroup(body));
	}

	private HttpMessageHeaderGroup createHeaderGroup(HttpMessageBody body) {
		
		List<HttpMessageHeader> headers = new ArrayList<>();
		
		body.contentType()
		.map(HttpContentType::contentType)
		.map(type -> new HttpMessageHeader(HttpMessageHeaderField.ContentType, type))
		.ifPresent(headers::add);
		
		int size = body.getBytes().length;
		
		if ( size > 0 ) {
			headers.add(new HttpMessageHeader(HttpMessageHeaderField.ContentLength, String.valueOf(size)));
		}
		
		return HttpMessageHeaderGroup.create(headers);
	}

}
