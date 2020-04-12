package com.shimizukenta.http;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import http.HttpMessageParseException;

public class HttpHeader {
	
	private static final String Separator = ":";
	private static final String SP = " ";
	
	private final String fieldName;
	private final String fieldValue;
	private final String line;
	
	private HttpHeader(String fieldName, String fieldValue, String line) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.line = line;
	}
	
	public static HttpHeader create(CharSequence fieldName, CharSequence fieldValue) {
		String fn = fieldName.toString();
		String fv = fieldValue.toString();
		return new HttpHeader(fn, fv, fn + Separator + SP + fv);
	}
	
	public static HttpHeader create(HttpHeaderField field, CharSequence fieldValue) {
		return create(field.fieldName(), fieldValue);
	}
	
	public static HttpHeader line(CharSequence line) throws HttpMessageParseException {
		String s = line.toString();
		String[] ss = s.split(Separator, 2);
		if ( ss.length < 2 ) {
			throw new HttpMessageParseException("Http-Header parse failed");
		}
		return new HttpHeader(ss[0], removeWhiteSpace(ss[1]), s);
	}
	
	public static HttpHeader fromBytes(byte[] bs) throws HttpMessageParseException {
		return line(new String(bs, StandardCharsets.UTF_8));
	}
	
	public String fieldName() throws HttpMessageParseException {
		return fieldName;
	}
	
	public String fieldValue() throws HttpMessageParseException {
		return fieldValue;
	}
	
	public String line() {
		return line;
	}
	
	@Override
	public String toString() {
		return line;
	}
	
	private static String removeWhiteSpace(CharSequence v) {
		return Objects.requireNonNull(v).toString().trim().replaceAll("\\s+", SP);
	}
	
	
	private static class SingletonHolder {
		private static final HttpHeader connectionKeepAlive = HttpHeader.create(HttpHeaderField.Connection, "Keep-Alive");
		private static final HttpHeader connectionClose = HttpHeader.create(HttpHeaderField.Connection, "close");
	}
	
	public static HttpHeader connectionKeepAlive() {
		return SingletonHolder.connectionKeepAlive;
	}
	
	public static HttpHeader connectionClose() {
		return SingletonHolder.connectionClose;
	}
	
	private static final DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
	private static final String nowZdt() {
		return ZonedDateTime.now(ZoneId.of("UTC")).format(rfc1123Formatter);
	}
	
	public static HttpHeader nowDate() {
		return HttpHeader.create(HttpHeaderField.Date, nowZdt());
	}
	
	public static HttpHeader nowLastModified() {
		return HttpHeader.create(HttpHeaderField.LastModified, nowZdt());
	}

}
