package com.shimizukenta.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface HttpMessageWriter {
	
	public static final byte[] CrLfBytes = new byte[] {0xD, 0xA};
	
	public void write(byte[] bs) throws InterruptedException, HttpWriteException;
	
	/**
	 * Write CRLF
	 * 
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void writeCrLf() throws InterruptedException, HttpWriteException {
		write(CrLfBytes);
	}
	
	/**
	 * write...<br />
	 * <br />
	 * Request-Line CRLF<br />
	 * *(Header CRLF)<br />
	 * CRLF<br />
	 * Body<br />
	 * 
	 * @param msg
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(HttpRequestMessage msg)
			throws InterruptedException, HttpWriteException {
		write(msg.requestLine(), msg.headerGroup());
		write(msg.body());
	}
	
	/**
	 * write...<br />
	 * <br />
	 * Status-Line CRLF<br />
	 * *(Header CRLF)<br />
	 * CRLF<br />
	 * Body<br />
	 * 
	 * @param msg
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(HttpResponseMessage msg)
			throws InterruptedException, HttpWriteException {
		write(msg.statusLine(), msg.headerGroup());
		write(msg.body());
	}
	
	/**
	 * write...<br />
	 * <br />
	 * Request-Line CRLF<br />
	 * *(Header CRLF)<br />
	 * CRLF<br />
	 * 
	 * @param requestLine
	 * @param headerGroup
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(
			HttpMessageRequestLine requestLine,
			HttpMessageHeaderGroup headerGroup)
					throws InterruptedException, HttpWriteException {
		
		try (
				ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
				) {
			
			os.write(requestLine.line().getBytes(StandardCharsets.US_ASCII));
			os.write(CrLfBytes);
			
			for ( String line : headerGroup.lines() ) {
				os.write(line.getBytes(StandardCharsets.US_ASCII));
				os.write(CrLfBytes);
			}
			
			os.write(CrLfBytes);
			
			write(os.toByteArray());
		}
		catch ( IOException e ) {
			throw new HttpWriteException(e);
		}
	}
	
	/**
	 * write...<br />
	 * <br />
	 * Status-Line CRLF<br />
	 * *(Header CRLF)<br />
	 * CRLF<br />
	 * 
	 * @param statusLine
	 * @param headerGroup
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(
			HttpMessageStatusLine statusLine,
			HttpMessageHeaderGroup headerGroup)
					throws InterruptedException, HttpWriteException {
		
		try (
				ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
				) {
			
			os.write(statusLine.line().getBytes(StandardCharsets.US_ASCII));
			os.write(CrLfBytes);
			
			for ( String line : headerGroup.lines() ) {
				os.write(line.getBytes(StandardCharsets.US_ASCII));
				os.write(CrLfBytes);
			}
			
			os.write(CrLfBytes);
			
			write(os.toByteArray());
		}
		catch ( IOException e ) {
			throw new HttpWriteException(e);
		}
	}
	
	/**
	 * write Request-Line CRLF
	 * 
	 * @param requestLine
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(HttpMessageRequestLine requestLine)
			throws InterruptedException, HttpWriteException {
		write(requestLine.line().getBytes(StandardCharsets.US_ASCII));
		writeCrLf();
	}
	
	/**
	 * write Status-Line CRLF
	 * 
	 * @param statusLine
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(HttpMessageStatusLine statusLine)
			throws InterruptedException, HttpWriteException {
		write(statusLine.line().getBytes(StandardCharsets.US_ASCII));
		writeCrLf();
	}
	
	/**
	 * write *(Header CRLF)
	 * 
	 * @param headerGroup
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(HttpMessageHeaderGroup headerGroup)
			throws InterruptedException, HttpWriteException {
		
		try (
				ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
				) {
			
			for ( String line : headerGroup.lines() ) {
				os.write(line.getBytes(StandardCharsets.US_ASCII));
				os.write(CrLfBytes);
			}
			
			write(os.toByteArray());
		}
		catch ( IOException e ) {
			throw new HttpWriteException(e);
		}
	}
	
	/**
	 * write Header CRLF
	 * 
	 * @param header
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(HttpMessageHeader header)
			throws InterruptedException, HttpWriteException {
		write(header.line().getBytes(StandardCharsets.US_ASCII));
		writeCrLf();
	}
	
	/**
	 * write Body
	 * 
	 * @param body
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void write(AbstractHttpMessageBody body)
			throws InterruptedException, HttpWriteException {
		write(body.getBytes());
	}
	
	/**
	 * write CHUNK<br />
	 * <br />
	 * Chunk-size CRLF<br />
	 * *(Chunk-data) CRLF<br />
	 * 
	 * @param chunkData
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void writeChunk(byte[] chunkData)
			throws InterruptedException, HttpWriteException {
		write(Integer.toHexString(chunkData.length).getBytes(StandardCharsets.US_ASCII));
		writeCrLf();
		write(chunkData);
		writeCrLf();
	}
	
	/**
	 * write LAST-CHUNK<br />
	 * <br />
	 * "0" CRLF<br />
	 * CRLF<br />
	 * 
	 * @throws InterruptedException
	 * @throws HttpWriteException
	 */
	default public void writeChunk()
			throws InterruptedException, HttpWriteException {
		write("0".getBytes(StandardCharsets.US_ASCII));
		writeCrLf();
		writeCrLf();
	}
	
}
