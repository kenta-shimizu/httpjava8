package httpBase;

import java.util.ArrayList;
import java.util.List;

public class HttpChunkBodyReader extends HttpBodyReader {
	
	private final List<byte[]> chunks = new ArrayList<>();
	private boolean completed;
	
	public HttpChunkBodyReader() {
		super();
		completed = false;
	}

	@Override
	public void put(byte[] bs) {
		if ( bs.length == 0 ) {
			this.completed = true;
		} else {
			chunks.add(bs);
		}
	}

	@Override
	public boolean completed() {
		return this.completed;
	}

	@Override
	public byte[] value() {
		
		if ( ! this.completed ) {
			throw new IllegalStateException("chunk not completed");
		}
		
		//TODO
		
		return null;
	}

}
