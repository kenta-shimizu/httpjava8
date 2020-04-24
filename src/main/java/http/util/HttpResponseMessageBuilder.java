package http.util;

import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageStatusLine;
import com.shimizukenta.http.HttpStatus;
import com.shimizukenta.http.HttpVersion;

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
			, HttpMessageHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		return new HttpResponseMessage(
				new HttpMessageStatusLine(version, statusCode, reasonPhrase)
				, headerGroup
				, body);
	}
	
	public HttpResponseMessage build(
			HttpVersion version
			, HttpStatus status
			, HttpMessageHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		return build(version, status.code(), status.reasonPhrase(), headerGroup, body);
	}
	
	public HttpResponseMessage build(HttpStatus status) {
		return build(status, HttpMessageHeaderGroup.empty());
	}
	
	public HttpResponseMessage build(HttpStatus status, HttpMessageHeaderGroup headerGroup) {
		return build(status, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpResponseMessage build(HttpStatus status, HttpMessageHeaderGroup headerGroup, HttpMessageBody body) {
		return build(getHttpVersion(), status, headerGroup, body);
	}
	
	abstract protected HttpVersion getHttpVersion();
	
	abstract public HttpResponseMessage buildOkResponse(HttpMessageBody body);
	
	public HttpResponseMessage buildOkResponse(HttpMessageHeaderGroup headerGroup, HttpMessageBody body) {
		return build(getHttpVersion(), HttpStatus.OK, headerGroup, body);
	}
	
	abstract public HttpResponseMessage buildOkHeadResponse(HttpMessageBody body);
	
	public HttpResponseMessage buildOkHeadResponse(HttpMessageHeaderGroup headerGroup) {
		return build(getHttpVersion(), HttpStatus.OK, headerGroup, HttpMessageBody.empty());
	}
	
}
