package com.shimizukenta.http;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpMessageBody extends AbstractHttpMessageBody {
	
	private HttpMessageBody(byte[] body) {
		super(body);
	}
	
	private static class SingletonHolder {
		private static final HttpMessageBody empty = new HttpMessageBody(new byte[0]);
	}
	
	public static HttpMessageBody empty() {
		return SingletonHolder.empty;
	}
	
	public static HttpMessageBody fromBytes(byte[] bs) {
		return new HttpMessageBody(bs);
	}
	
	public static HttpMessageBody fromBytes(InputStream strm) throws IOException {
		
		try (
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				) {
			
			for ( ;; ) {
				
				int r = strm.read();
				
				if ( r < 0 ) {
					break;
				}
				
				os.write(r);
			}
			
			return fromBytes(os.toByteArray());
		}
	}
	
	public static HttpMessageBody fromText(CharSequence text, Charset charset) {
		return fromBytes(text.toString().getBytes(charset));
	}
	
	public static HttpMessageBody fromText(CharSequence text) {
		return fromBytes(text.toString().getBytes());
	}
	
	public static HttpMessageBody fromText(Reader reader, Charset charset) throws IOException {
		return fromText(fromReaderToText(reader), charset);
	}
	
	public static HttpMessageBody fromText(Reader reader) throws IOException {
		return fromText(fromReaderToText(reader));
	}
	
	private static String fromReaderToText(Reader reader) throws IOException {
		
		try (
				CharArrayWriter writer = new CharArrayWriter();
				) {
			
			for ( ;; ) {
				
				int r = reader.read();
				
				if ( r < 0 ) {
					break;
				}
				
				writer.write(r);
			}
			
			return writer.toString();
		}
	}
	
	public static HttpMessageBody fromFile(Path path) throws IOException {
		return fromBytes(Files.readAllBytes(path));
	}
}
