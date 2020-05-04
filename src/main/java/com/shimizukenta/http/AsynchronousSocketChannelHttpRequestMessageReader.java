package com.shimizukenta.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsynchronousSocketChannelHttpRequestMessageReader extends AbstractHttpRequestMessageReader {
	
	private static final int ByteBufferSize = 4096;
	private static final int ByteArrayOutputStreamSize = 1024;
	
	private static final byte LF = (byte)0xA;
	
	private final ByteBuffer buffer;
	
	private final AsynchronousSocketChannel channel;
	private final long timeout;
	
	public AsynchronousSocketChannelHttpRequestMessageReader(AsynchronousSocketChannel channel, long timeout) {
		super();
		
		this.channel = channel;
		this.timeout = timeout;
		
		this.buffer = ByteBuffer.allocate(ByteBufferSize);
		((Buffer)(this.buffer)).flip();
	}

	@Override
	public HttpRequestMessage read() throws InterruptedException, HttpReadException {
		
		try {
			
			final HttpMessageRequestLine requestLine = HttpMessageRequestLine.fromBytes(readLine());
			
			final List<String> headerLines = new ArrayList<>();
			
			for ( ;; ) {
				
				byte[] bs = readLine();
				
				String s = new String(bs, StandardCharsets.US_ASCII);
				
				if ( s.trim().isEmpty() ) {
					break;
				}
				
				headerLines.add(s);
			}
			
			HttpMessageHeaderGroup headerGroup = HttpMessageHeaderGroup.fromLines(headerLines);
			
			Optional<String> opContentLength = headerGroup.getFieldValue(HttpMessageHeaderField.ContentLength);
			
			boolean isChunked = headerGroup.getFieldValue(HttpMessageHeaderField.TransferEncoding)
					.map(v -> (v.split(";", 2))[0])
					.filter(v -> v.equalsIgnoreCase("chunked"))
					.isPresent();
			
			if ( isChunked ) {
				
				if ( opContentLength.isPresent() ) {
					throw new HttpReadException("Content-Length with Chunk");
				}
				
				return readChunkMessage(requestLine, headerLines);
			}
			
			try {
				int len = opContentLength
						.map(v -> Integer.valueOf(v))
						.orElse(Integer.valueOf(0))
						.intValue();
				
				return readByteMessage(requestLine, headerGroup, len);
			}
			catch ( NumberFormatException e ) {
				throw new HttpReadException(e);
			}
		}
		catch ( TimeoutException e ) {
			return null;
		}
		catch ( HttpMessageParseException e ) {
			throw new HttpReadException(e);
		}
	}
	
	private byte readByte() throws InterruptedException, TimeoutException, HttpReadException {
		
		if ( ! buffer.hasRemaining() ) {
			
			((Buffer)buffer).clear();
			
			Future<Integer> f = channel.read(buffer);
			
			try {
				int r = f.get(timeout, TimeUnit.SECONDS).intValue();
				
				if ( r < 0 ) {
					throw new HttpReadException("Channel closed");
				}
				
				((Buffer)buffer).flip();
			}
			catch ( InterruptedException e ) {
				f.cancel(true);
				throw e;
			}
			catch ( TimeoutException e ) {
				f.cancel(true);
				throw e;
			}
			catch ( ExecutionException e ) {
				throw new HttpReadException(e.getCause());
			}
		}
		
		return buffer.get();
	}
	
	private byte[] readLine() throws InterruptedException, TimeoutException, HttpReadException {
		
		try (
				ByteArrayOutputStream os = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
				) {
			
			for ( ;; ) {
				
				byte b = readByte();
				
				os.write(b);
				
				if ( b == LF ) {
					break;
				}
			}
			
			return os.toByteArray();
		}
		catch ( IOException e ) {
			throw new HttpReadException(e);
		}
	}
	
	private HttpRequestMessage readByteMessage(
			HttpMessageRequestLine requestLine,
			HttpMessageHeaderGroup headerGroup,
			int contentLength)
					throws InterruptedException, TimeoutException, HttpReadException {
		
		if ( contentLength < 0 ) {
			throw new HttpReadException("Content-Length < 0");
		}
		
		final HttpMessageBody body;
		
		if ( contentLength == 0 ) {
			
			body = HttpMessageBody.empty();
			
		} else {
			
			try (
					ByteArrayOutputStream bodyData = new ByteArrayOutputStream(contentLength);
					) {
				
				for ( int i = 0; i < contentLength; ++i ) {
					bodyData.write(readByte());
				}
				
				body = HttpMessageBody.fromBytes(bodyData.toByteArray());
			}
			catch ( IOException e ) {
				throw new HttpReadException(e);
			}
		}
		
		return new HttpRequestMessage(requestLine, headerGroup, body);
	}
	
	private HttpRequestMessage readChunkMessage(
			HttpMessageRequestLine requestLine,
			List<String> headerLines)
					throws InterruptedException, TimeoutException,
					HttpReadException, HttpMessageParseException {
		
		try (
				ByteArrayOutputStream chunkData = new ByteArrayOutputStream(ByteArrayOutputStreamSize);
				) {
			
			for ( ;; ) {
				
				String line = new String(readLine(), StandardCharsets.US_ASCII);
				
				String[] ss = line.split(";", 2);
				
				int chunkSize = Integer.parseInt(ss[0].trim(), 16);
				
				if ( chunkSize == 0 ) {
					break;
				}
				
				for ( int i = 0 ; i < chunkSize ; ++i ) {
					chunkData.write(readByte());
				}
			}
			
			/* read trailers */
			for ( ;; ) {
				
				String line = new String(readLine(), StandardCharsets.US_ASCII);
				
				if ( line.trim().isEmpty() ) {
					break;
				}
				
				headerLines.add(line);
			}
			
			return new HttpRequestMessage(
					requestLine,
					HttpMessageHeaderGroup.fromLines(headerLines),
					HttpMessageBody.fromBytes(chunkData.toByteArray()));
		}
		catch ( NumberFormatException e ) {
			throw new HttpReadException(e);
		}
		catch ( IOException e ) {
			throw new HttpReadException(e);
		}
	}
	

}
