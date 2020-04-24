package http.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageParseException;

public class HttpMessageChunkBodyReader implements HttpMessageBodyReadable {
	
	private final ByteArrayOutputStream os;
	private final List<byte[]> lines;
	private final CrLfLineReader lr = new CrLfLineReader(4096);
	private final List<byte[]> trailer;
	
	private boolean completed;
	private boolean readChunkData;
	private boolean lastChunk;
	private int chunkDataSize;
	
	private HttpMessageHeaderGroup headerGroup;
	
	public HttpMessageChunkBodyReader() {
		super();
		
		this.os = new ByteArrayOutputStream(4096);
		this.lines = new ArrayList<>();
		this.trailer = new ArrayList<>();
		
		this.completed = false;
		this.readChunkData = false;
		this.lastChunk = false;
		this.chunkDataSize = 0;
		
		this.headerGroup = null;
	}
	
	@Override
	public boolean put(ByteBuffer buffer) throws HttpMessageParseException {
		
		synchronized ( this ) {
			
			for ( ;; ) {
				
				Optional<byte[]> op = lr.put(buffer);
				
				if ( op.isPresent() ) {
					
					byte[] bs = op.get();
					
					lines.add(bs);
					
					if ( lastChunk ) {
						
						if ( bs.length == 0 ) {
							
							completed = true;
							return completed;
							
						} else {
							
							trailer.add(bs);
						}
						
					} else {
						
						if ( readChunkData ) {
							
							try {
								if ( bs.length != chunkDataSize ) {
									throw new HttpMessageParseException("chunk-data-size unmatch");
								}
								
								os.write(bs);
							}
							catch (IOException e) {
								throw new HttpMessageParseException(e);
							}
							
							readChunkData = false;
							
						} else {
							
							String[] ss = new String(bs).split(";");
							String s0 = ss[0];
							
							if ( s0.equals("0") ) {
								
								lastChunk = true;
								
							} else {
								
								try {
									chunkDataSize = Integer.parseInt("0" + s0);
								}
								catch ( NumberFormatException e ) {
									throw new HttpMessageParseException(e);
								}
								
								readChunkData = true;
							}
						}
					}
					
				} else {
					
					return completed;
				}
			}
		}
	}

	@Override
	public boolean completed() {
		synchronized ( this ) {
			return this.completed;
		}
	}

	@Override
	public HttpMessageBody getHttpMessageBody() {
		
		synchronized ( this ) {
			
			if ( ! completed() ) {
				throw new IllegalStateException("chunk not completed");
			}
			
			return new HttpMessageBody(os.toByteArray());
		}
	}

	@Override
	public HttpMessageHeaderGroup trailer() {
		
		synchronized ( this ) {
			
			if ( ! completed() ) {
				throw new IllegalStateException("chunk not completed");
			}
			
			if ( headerGroup == null ) {
				
				List<String> lines = trailer.stream()
						.map(bs -> new String(bs, StandardCharsets.US_ASCII))
						.collect(Collectors.toList());
				
				headerGroup = HttpMessageHeaderGroup.lines(lines);
			}
			
			return headerGroup;
		}
	}

}
