package http.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shimizukenta.http.HttpHeader;
import com.shimizukenta.http.HttpHeaderField;

import http.HttpMessageParseException;

public class HttpHeaderGroup implements Cloneable {
	
	private static final String CRLF = "\r\n";
	
	private List<HttpHeader> headers;
	private List<String> lines;
	
	private byte[] parsedBytes;
	private String parsedString;
	
	private HttpHeaderGroup() {
		headers = null;
		lines = null;
		parsedBytes = null;
		parsedString = null;
	}
	
	private static class SingletonHolder {
		private static final HttpHeaderGroup empty = HttpHeaderGroup.lines(Collections.emptyList());
	}
	
	public static HttpHeaderGroup empty() {
		return SingletonHolder.empty;
	}
	
	public static HttpHeaderGroup create(List<HttpHeader> headers) {
		HttpHeaderGroup inst = new HttpHeaderGroup();
		inst.headers = Collections.unmodifiableList(headers);
		return inst;
	}
	
	public static HttpHeaderGroup lines(List<? extends CharSequence> lines) {
		HttpHeaderGroup inst = new HttpHeaderGroup();
		inst.lines = lines.stream().map(CharSequence::toString).collect(Collectors.toList());
		return inst;
	}
	
	@Override
	public Object clone() {
		
		synchronized ( this ) {
			
			HttpHeaderGroup inst = new HttpHeaderGroup();
			
			if ( this.headers != null ) {
				inst.headers = this.headers.stream()
						.map(h -> ((HttpHeader)h.clone()))
						.collect(Collectors.toList());
			}
			
			if ( this.lines != null) {
				inst.lines = Collections.unmodifiableList(this.lines);
			}
			
			if ( this.parsedBytes != null ) {
				inst.parsedBytes = this.parsedBytes;
			}
			
			if ( this.parsedString != null ) {
				inst.parsedString = this.parsedString;
			}
			
			return inst;
		}
	}
	
	private static final String SP = " ";
	private static final String HT = "\t";
	
	public List<HttpHeader> headers() {
		
		synchronized ( this ) {
			
			if ( headers == null ) {
				
				List<String> ll = new ArrayList<String>(lines);
				
				for ( int i = ll.size(); i > 1; ) {
					
					 -- i;
					 
					 String s = ll.get(i);
					 
					 if ( s.startsWith(SP) || s.startsWith(HT) ) {
						 
						 int prev = i - 1;
						 
						 String a = ll.get(prev) + ll.remove(i);
						 ll.set(prev, a);
					 }
				}
				
				headers = ll.stream().map(HttpHeader::new).collect(Collectors.toList());
			}
			
			return headers;
		}
	}
	
	public Optional<String> getFieldValue(CharSequence fieldName) throws HttpMessageParseException {
		
		String s = fieldName.toString();
		
		for ( HttpHeader hh : headers() ) {
			
			if ( hh.fieldName().equalsIgnoreCase(s) ) {
				
				return Optional.of(hh.fieldValue());
			}
		}
		
		return Optional.empty();
	}
	
	public Optional<String> getFieldValue(HttpHeaderField field) throws HttpMessageParseException {
		return getFieldValue(field.fieldName());
	}
	
	private static final byte[] CRLFBYTES = CRLF.getBytes(StandardCharsets.US_ASCII);
	
	public byte[] getBytes() throws HttpMessageParseException {
		
		synchronized (this ) {
			
			if ( parsedBytes == null ) {
				
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
					
					for ( HttpHeader h : headers() ) {
						
						byte[] bs = h.toString().getBytes(StandardCharsets.US_ASCII);
						
						os.write(bs);
						os.write(CRLFBYTES);
					}
					
					parsedBytes = os.toByteArray();
				}
				catch ( IOException e ) {
					throw new HttpMessageParseException(e);
				}
			}
			
			return parsedBytes;
		}
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( parsedString == null ) {
				
				if ( lines != null ) {
					
					StringBuilder sb = new StringBuilder();
					
					lines.stream().forEach(line -> {
						sb.append(line).append(CRLF);
					});
					
					parsedString = sb.toString();
					
				} else if ( headers != null ) {
					
					StringBuilder sb = new StringBuilder();
					
					headers.stream()
					.map(HttpHeader::toString)
					.forEach(s -> {
						sb.append(s).append(CRLF);
					});
					
					parsedString = sb.toString();
					
				} else {
					
					return "";
				}
			}
			
			return parsedString;
		}
	}
}
