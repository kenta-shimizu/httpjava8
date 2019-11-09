package httpUtil;

import java.util.Arrays;
import java.util.List;

import httpBase.HttpHeader;
import httpBase.HttpHeaderField;
import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpMethod;
import httpBase.HttpRequestMessagePack;
import httpBase.HttpVersion;

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
	public HttpRequestMessagePack createGet(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, r.headerHost())
				, HttpHeader.connectionKeepAlive()
				);
		
		return this.create(
				r.serverSocketAddress()
				, HttpMethod.GET.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, HttpMessageBody.empty());
	}
	
	@Override
	public HttpRequestMessagePack createHead(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, r.headerHost())
				, HttpHeader.connectionKeepAlive()
				);
		
		return this.create(
				r.serverSocketAddress()
				, HttpMethod.HEAD.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, HttpMessageBody.empty());
	}
	
	@Override
	public HttpRequestMessagePack createPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException {
		
		AbsoluteUriParseResult r = parseUri(absoluteUri);
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, r.headerHost())
				, HttpHeader.connectionKeepAlive()
				, new HttpHeader(HttpHeaderField.ContentLength, String.valueOf(body.getBytes().length))
				);
		
		return this.create(
				r.serverSocketAddress()
				, HttpMethod.POST.toString()
				, r.requestLineUri()
				, HttpHeaderGroup.create(headers)
				, body);
	}

}
