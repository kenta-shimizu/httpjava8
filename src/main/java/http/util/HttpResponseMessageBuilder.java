package http.util;

import com.shimizukenta.http.HttpStatus;
import com.shimizukenta.http.HttpStatusLine;
import com.shimizukenta.http.HttpVersion;

import http.base.HttpHeaderGroup;
import http.base.HttpMessageBody;
import http.base.HttpResponseMessage;

public abstract class HttpResponseMessageBuilder {

	protected HttpResponseMessageBuilder() {
		/* Nothing */
	}
	
	public HttpResponseMessage build(
			HttpVersion version
			, int statusCode
			, CharSequence reasonPhrase
			, HttpHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		return new HttpResponseMessage(
				new HttpStatusLine(version, statusCode, reasonPhrase)
				, headerGroup
				, body);
	}
	
	public HttpResponseMessage build(
			HttpVersion version
			, HttpStatus status
			, HttpHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		return build(version, status.code(), status.reasonPhrase(), headerGroup, body);
	}
	
	public HttpResponseMessage build(HttpStatus status) {
		return build(status, HttpHeaderGroup.empty());
	}
	
	public HttpResponseMessage build(HttpStatus status, HttpHeaderGroup headerGroup) {
		return build(status, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpResponseMessage build(HttpStatus status, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		return build(getHttpVersion(), status, headerGroup, body);
	}
	
	abstract protected HttpVersion getHttpVersion();
	
	abstract public HttpResponseMessage buildOkResponse(HttpMessageBody body);
	
	public HttpResponseMessage buildOkResponse(HttpHeaderGroup headerGroup, HttpMessageBody body) {
		return build(getHttpVersion(), HttpStatus.OK, headerGroup, body);
	}
	
	abstract public HttpResponseMessage buildOkHeadResponse(HttpMessageBody body);
	
	public HttpResponseMessage buildOkHeadResponse(HttpHeaderGroup headerGroup) {
		return build(getHttpVersion(), HttpStatus.OK, headerGroup, HttpMessageBody.empty());
	}
	
}
