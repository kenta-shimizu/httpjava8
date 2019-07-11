package httpBase;

public class HttpBytesBodyReader extends HttpBodyReader {
	
	private final int length;
	
	public HttpBytesBodyReader(int length) {
		super();
		this.length = length;
	}

	@Override
	public void put(byte[] bs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean completed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] value() {
		// TODO Auto-generated method stub
		return null;
	}

}
