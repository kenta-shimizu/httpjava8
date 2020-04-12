package http.util;

import java.util.Arrays;
import java.util.List;

import com.shimizukenta.http.HttpHeader;
import com.shimizukenta.http.HttpHeaderField;
import com.shimizukenta.http.HttpMethod;
import com.shimizukenta.http.HttpVersion;

import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpRequestMessagePack;

public class HttpRequestMessageBuilderVersion1p1 extends HttpRequestMessageBuilder {

	public HttpRequestMessageBuilderVersion1p1() {
		super();
	}
	
	@Override
	public HttpVersion getHttpVersion() {
		return HttpVersion.HTTP1_1;
	}
	
	private AbsoluteUriParseResult parseUri(CharSequence absoluteUri) throws AbsoluteUriParseException {
		return AbsoluteUriParser.getInstance().parse(absoluteUri);
	}
	
	@Override
	public HttpRequestMessagePack buildGet(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, r.headerHost())
				, HttpHeader.connectionKeepAlive()
				);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.GET.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, HttpMessageBody.empty());
	}
	
	@Override
	public HttpRequestMessagePack buildHead(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, r.headerHost())
				, HttpHeader.connectionKeepAlive()
				);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.HEAD.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, HttpMessageBody.empty());
	}
	
	@Override
	public HttpRequestMessagePack buildPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, r.headerHost())
				, HttpHeader.connectionKeepAlive()
				, new HttpHeader(HttpHeaderField.ContentLength, String.valueOf(body.getBytes().length))
				);
		
		return this.build(
				r.serverSocketAddress()
				, HttpMethod.POST.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, body);
	}

}
