package http.util;

import java.util.Arrays;
import java.util.List;

import com.shimizukenta.httpserver.HttpMessageHeader;
import com.shimizukenta.httpserver.HttpMessageHeaderField;
import com.shimizukenta.httpserver.HttpMessageHeaderGroup;
import com.shimizukenta.httpserver.HttpMethod;
import com.shimizukenta.httpserver.HttpVersion;

import http.base.HttpMessageBody;
import http.base.HttpRequestMessagePack;

public class HttpRequestMessageBuilderVersion1p0 extends HttpRequestMessageBuilder {

	public HttpRequestMessageBuilderVersion1p0() {
		super();
	}
	
	@Override
	public HttpVersion getHttpVersion() {
		return HttpVersion.HTTP1_0;
	}
	
	private AbsoluteUriParseResult parseUri(CharSequence absoluteUri) throws AbsoluteUriParseException {
		return AbsoluteUriParser.getInstance().parse(absoluteUri);
	}
	
	@Override
	public HttpRequestMessagePack buildGet(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.GET.toString()
				, r.requestLineUri()
				, HttpMessageHeaderGroup.empty()
				, HttpMessageBody.empty());
	}

	@Override
	public HttpRequestMessagePack buildHead(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.HEAD.toString()
				, r.requestLineUri()
				, HttpMessageHeaderGroup.empty()
				, HttpMessageBody.empty());
	}

	@Override
	public HttpRequestMessagePack buildPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpMessageHeader> headers = Arrays.asList(
				new HttpMessageHeader(HttpMessageHeaderField.ContentLength, String.valueOf(body.getBytes().length))
				);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.POST.toString()
				, r.requestLineUri()
				, HttpMessageHeaderGroup.create(headers)
				, body);
	}

}
