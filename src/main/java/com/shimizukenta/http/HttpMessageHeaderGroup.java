package com.shimizukenta.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpMessageHeaderGroup {
	
	private final List<HttpMessageHeader> headers;
	private final List<String> lines;
	private String parsedString;
	
	private HttpMessageHeaderGroup(List<HttpMessageHeader> headers, List<String> lines) {
		this.headers = headers;
		this.lines = lines;
		this.parsedString = null;
	}
	
	private static class SingletonHolder {
		private static final HttpMessageHeaderGroup empty = new HttpMessageHeaderGroup(Collections.emptyList(), Collections.emptyList());
	}
	
	public static HttpMessageHeaderGroup empty() {
		return SingletonHolder.empty;
	}
	
	public static HttpMessageHeaderGroup create(List<HttpMessageHeader> headers) {
		
		final List<String> lines = headers.stream()
				.map(HttpMessageHeader::line)
				.collect(Collectors.toList());
		
		return new HttpMessageHeaderGroup(headers, lines);
	}
	
	private static final String SP = " ";
	private static final String HT = "\t";
	
	public static HttpMessageHeaderGroup fromLines(List<? extends CharSequence> lines)
			throws HttpMessageParseException {
		
		final List<String> strLines = lines.stream()
				.map(CharSequence::toString)
				.collect(Collectors.toList());
		
		final List<HttpMessageHeader> headers = new ArrayList<>();
		
		{
			final List<String> ll = new ArrayList<>(strLines);
			
			for ( int i = ll.size(); i > 1; ) {
				
				-- i;
				
				String s = ll.get(i);
				
				if ( s.startsWith(SP) || s.startsWith(HT) ) {
					
					int prev = i - 1;
					
					String a = ll.get(prev) + ll.remove(i);
					ll.set(prev, a);
				}
			}
			
			for ( String line : ll ) {
				headers.add(HttpMessageHeader.fromLine(line));
			}
		}
		
		return new HttpMessageHeaderGroup(headers, strLines);
	}
	
	
	/**
	 * 
	 * @return HttpHeaders
	 */
	public List<HttpMessageHeader> headers() {
		return Collections.unmodifiableList(headers);
	}
	
	/**
	 * 
	 * @return Text-Header-Lines
	 */
	public List<String> lines() {
		return lines;
	}
	
	/**
	 * Seek Field Name in headers
	 * 
	 * @param fieldName
	 * @return Header Field Value if exist
	 */
	public Optional<String> getFieldValue(CharSequence fieldName) {
		
		if ( fieldName != null ) {
			
			String s = fieldName.toString();
			
			for ( HttpMessageHeader hh : headers ) {
				
				if ( hh.fieldName().equalsIgnoreCase(s) ) {
					
					return Optional.of(hh.fieldValue());
				}
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Seek Field Name in headers
	 * 
	 * @param field
	 * @return Header Field Value if exist
	 */
	public Optional<String> getFieldValue(HttpMessageHeaderField field) {
		return getFieldValue(field.fieldName());
	}
	
	
	private static final String CRLF = "\r\n";
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( parsedString == null ) {
				
				StringBuilder sb = new StringBuilder();
				
				lines.stream().forEach(line -> {
					sb.append(line).append(CRLF);
				});
				
				parsedString = sb.toString();
			}
			
			return parsedString;
		}
	}
}
