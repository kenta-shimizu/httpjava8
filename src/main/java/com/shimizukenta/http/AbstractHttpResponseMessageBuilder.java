package com.shimizukenta.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public abstract class AbstractHttpResponseMessageBuilder implements HttpResponseMessageBuilder {
	
	private final AbstractHttpResponseMessageBuilderConfig config;
	
	public AbstractHttpResponseMessageBuilder(AbstractHttpResponseMessageBuilderConfig config) {
		this.config = config;
	}
	
	/**
	 * Prototype-pattern<br />
	 * 
	 * @return Http-Version
	 */
	abstract protected HttpVersion version();
	
	@Override
	public HttpResponseMessage build(
			HttpStatus status,
			HttpMessageHeaderGroup headerGroup,
			AbstractHttpMessageBody body) {
		
		return new HttpResponseMessage(
				HttpMessageStatusLine.create(version(), status),
				headerGroup,
				body);
	}
	
	
	private static final int ByteArrayOutputStreamSize = 256 * 64;
	
	protected static byte[] encodeGZIP(byte[] bs) throws IOException {
		
		try (
				ByteArrayOutputStream baos = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
				) {
			
			try (
					GZIPOutputStream gzipos = new GZIPOutputStream(baos);
					) {
				
				gzipos.write(bs);
			}
			
			return baos.toByteArray();
		}
	}
	
	protected static byte[] encodeDeflate(byte[] bs) throws IOException {
		
		final Deflater comp = new Deflater();
		
		try {
			
			comp.setInput(bs);
			comp.finish();
			
			byte[] buffer = new byte[4096];
			
			try (
					ByteArrayOutputStream baos = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
					) {
				
				for ( ;; ) {
					
					int len = comp.deflate(buffer);
					
					if ( len > 0 ) {
						
						baos.write(buffer, 0, len);
						
					} else {
						
						return baos.toByteArray();
					}
				}
			}
		}
		finally {
			comp.end();
		}
	}
	
	
	private static final DateTimeFormatter rfc1123Formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	private static ZonedDateTime nowZdt() {
		return ZonedDateTime.now(ZoneId.of("UTC"));
	}
	
	protected void addDateHeader(List<HttpMessageHeader> headers) {
		
		if ( config.addDateHeader() ) {
			
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.Date,
					nowZdt().format(rfc1123Formatter)));
		}
	}
	
	/**
	 * Add Now
	 * 
	 * @param headers
	 */
	protected void addLastModifiedHeader(List<HttpMessageHeader> headers) {
		
		if ( config.addLastModifiedHeader() ) {
			
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.LastModified,
					nowZdt().format(rfc1123Formatter)));
		}
	}
	
	/**
	 * Add File's Last Modified
	 * 
	 * @param headers
	 * @param filePath
	 * @throws IOException
	 */
	protected void addLastModifiedHeader(
			List<HttpMessageHeader> headers,
			Path filePath)
					throws IOException {
		
		if ( config.addLastModifiedHeader() ) {
			
			FileTime ft = Files.getLastModifiedTime(filePath);
			ZonedDateTime lmt = ZonedDateTime.ofInstant(ft.toInstant(), ZoneId.of("UTC"));
			
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.LastModified,
					lmt.format(rfc1123Formatter)));
		}
	}
	
	/**
	 * Add Server
	 * 
	 * @param headers
	 */
	protected void addServerHeader(List<HttpMessageHeader> headers) {
		config.serverName()
		.map(n -> HttpMessageHeader.create(HttpMessageHeaderField.Server, n))
		.ifPresent(headers::add);
	}
	
}
