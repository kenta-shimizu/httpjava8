package httpUtil;

import java.util.Arrays;
import java.util.List;

import httpBase.HttpHeader;
import httpBase.HttpHeaderField;
import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpMethod;
import httpBase.HttpRequestMessage;
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
	public HttpRequestMessage createGet(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, parseUri(absoluteUri).headerHost())
				, HttpHeader.connectionKeepAlive()
				);
		
		return this.create(
				HttpMethod.GET.toString()
				, parseUri(absoluteUri).requestLineUri()
				, HttpHeaderGroup.create(headers)
				, HttpMessageBody.empty());
	}
	
	@Override
	public HttpRequestMessage createHead(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, parseUri(absoluteUri).headerHost())
				, HttpHeader.connectionKeepAlive()
				);
		
		return this.create(
				HttpMethod.HEAD.toString()
				, parseUri(absoluteUri).requestLineUri()
				, HttpHeaderGroup.create(headers)
				, HttpMessageBody.empty());
	}
	
	@Override
	public HttpRequestMessage createPost(CharSequence absoluteUri, HttpMessageBody body) throws AbsoluteUriParseException {
		
		List<HttpHeader> headers = Arrays.asList(
				new HttpHeader(HttpHeaderField.Host, parseUri(absoluteUri).headerHost())
				, HttpHeader.connectionKeepAlive()
				, new HttpHeader(HttpHeaderField.ContentLength, String.valueOf(body.getBytes().length))
				);
		
		return this.create(
				HttpMethod.POST.toString()
				, parseUri(absoluteUri).requestLineUri()
				, HttpHeaderGroup.create(headers)
				, body);
	}

}
