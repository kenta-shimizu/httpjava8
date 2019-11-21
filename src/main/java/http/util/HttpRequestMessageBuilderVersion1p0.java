package http.util;

import java.util.Arrays;
import java.util.List;

import http.base.HttpHeader;
import http.base.HttpHeaderField;
import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpMethod;
import http.base.HttpRequestMessagePack;
import http.base.HttpVersion;

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
				, HttpHeaderGroup.empty()
				, HttpMessageBody.empty());
	}

	@Override
	public HttpRequestMessagePack buildHead(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.HEAD.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.empty()
				, HttpMessageBody.empty());
	}

	@Override
	public HttpRequestMessagePack buildPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.ContentLength, String.valueOf(body.getBytes().length))
				);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.POST.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, body);
	}

}
