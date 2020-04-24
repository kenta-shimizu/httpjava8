package com.shimizukenta.http;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpMessageHeader {
	
	private static final String Separator = ":";
	private static final String SP = " ";
	
	private final String fieldName;
	private final String fieldValue;
	private final String line;
	
	private HttpMessageHeader(String fieldName, String fieldValue, String line) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.line = line;
	}
	
	public static HttpMessageHeader create(CharSequence fieldName, CharSequence fieldValue) {
		String fn = fieldName.toString();
		String fv = fieldValue.toString();
		return new HttpMessageHeader(fn, fv, fn + Separator + SP + fv);
	}
	
	public static HttpMessageHeader create(HttpMessageHeaderField field, CharSequence fieldValue) {
		return create(field.fieldName(), fieldValue);
	}
	
	public static HttpMessageHeader fromLine(CharSequence line) throws HttpMessageParseException {
		String strLine = line.toString();
		String[] ss = strLine.split(Separator, 2);
		if ( ss.length != 2 ) {
			throw new HttpMessageParseException("Http-Header parse failed \"" + strLine + "\"");
		}
		return new HttpMessageHeader(ss[0], removeWhiteSpace(ss[1]), strLine);
	}
	
	public static HttpMessageHeader fromBytes(byte[] bs) throws HttpMessageParseException {
		return fromLine(new String(bs, StandardCharsets.UTF_8));
	}
	
	public String fieldName() {
		return fieldName;
	}
	
	public String fieldValue() {
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
		private static final HttpMessageHeader connectionKeepAlive = HttpMessageHeader.create(HttpMessageHeaderField.Connection, "Keep-Alive");
		private static final HttpMessageHeader connectionClose = HttpMessageHeader.create(HttpMessageHeaderField.Connection, "close");
	}
	
	public static HttpMessageHeader connectionKeepAlive() {
		return SingletonHolder.connectionKeepAlive;
	}
	
	public static HttpMessageHeader connectionClose() {
		return SingletonHolder.connectionClose;
	}
	
}
