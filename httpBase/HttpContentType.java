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
		
		private static HttpContentType undefined = new HttpContentType("application/octet-stream", "exe");
		
		private static final Collection<HttpContentType> inst = Arrays.asList(
				x("text/plain", "txt", "log"),
				x("text/csv", "csv"),
				x("text/html", "html", "htm"),
				x("text/css", "css"),
				x("text/javascript", "js"),
				x("text/xml", "xml"),
				x("application/json", "json"),
				x("application/javascript", "jsonp"),
				x("application/pdf", "pdf"),
				x("application/xhtml+xml", "xhtml"),
				x("application/vnd.ms-excel", "xls"),
				x("application/vnd.ms-powerpoint", "ppt"),
				x("application/msword", "doc"),
				x("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
				x("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
				x("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
				x("image/jpeg", "jpg", "jpeg"),
				x("image/png", "png"),
				x("image/gif", "gif"),
				x("image/bmp", "bmp"),
				x("image/tiff", "tiff", "tif"),
				x("image/vnd.microsoft.icon", "ico"),
				x("application/zip", "zip"),
				x("application/x-lzh", "lzh"),
				x("application/x-tar", "tar"),
				x("audio/mpeg", "mp3"),
				x("video/mp4", "mp4"),
				x("video/mpeg", "mpeg"),
				x("video/x-msvideo", "avi"),
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
