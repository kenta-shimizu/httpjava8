package httpUtil;

import httpBase.HttpHeaderGroup;
import httpBase.HttpMessageBody;
import httpBase.HttpResponseMessage;
import httpBase.HttpStatus;
import httpBase.HttpStatusLine;
import httpBase.HttpVersion;

public abstract class HttpResponseMessageBuilder {

	protected HttpResponseMessageBuilder() {
		/* Nothing */
	}
	
	public HttpResponseMessage create(
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
	
	public HttpResponseMessage create(
			HttpVersion version
			, HttpStatus status
			, HttpHeaderGroup headerGroup
			, HttpMessageBody body) {
		
		return create(version, status.code(), status.reasonPhrase(), headerGroup, body);
	}
	
	public HttpResponseMessage create(HttpStatus status) {
		return create(status, HttpHeaderGroup.empty());
	}
	
	public HttpResponseMessage create(HttpStatus status, HttpHeaderGroup headerGroup) {
		return create(status, headerGroup, HttpMessageBody.empty());
	}
	
	public HttpResponseMessage create(HttpStatus status, HttpHeaderGroup headerGroup, HttpMessageBody body) {
		return create(getHttpVersion(), status, headerGroup, body);
	}
	
	abstract protected HttpVersion getHttpVersion();
	
	abstract public HttpResponseMessage createOkResponse(HttpMessageBody body);
	
	public HttpResponseMessage createOkResponse(HttpHeaderGroup headerGroup, HttpMessageBody body) {
		return create(getHttpVersion(), HttpStatus.OK, headerGroup, body);
	}
	
	abstract public HttpResponseMessage createOkHeadResponse(HttpMessageBody body);
	
	public HttpResponseMessage createOkHeadResponse(HttpHeaderGroup headerGroup) {
		return create(getHttpVersion(), HttpStatus.OK, headerGroup, HttpMessageBody.empty());
	}
	
}
