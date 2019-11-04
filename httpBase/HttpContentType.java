package httpBase;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpContentType {

	private final String contentType;
	private final Collection<String> extensions;
	
	public HttpContentType(CharSequence contentType, CharSequence... extensions) {
		this.contentType = contentType.toString();
		this.extensions = Stream.of(extensions).map(CharSequence::toString).collect(Collectors.toList());
	}
	
	public String contentType() {
		return contentType;
	}
	
	private static class SingletonHolder {
		
		private static HttpContentType undefined = new HttpContentType("application/octet-stream", "");
		
		private static final Collection<HttpContentType> inst = Arrays.asList(
				x("text/html", "html", "htm"),
				undefined
				);
		
		private static HttpContentType x(String t, String... ss) {
			return new HttpContentType(t, ss);
		}
	}
	
	public static HttpContentType get(CharSequence extension) {
		
		String s = extension.toString();
		
		for ( HttpContentType t : SingletonHolder.inst ) {
			
			for ( String ext : t.extensions ) {
				
				if ( ext.equalsIgnoreCase(s) ) {
					
					return t;
				}
			}
		}
		
		return SingletonHolder.undefined;
	}
	
}
