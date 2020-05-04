package example2;

import com.shimizukenta.http.AbstractHttpServerService;
import com.shimizukenta.http.HttpMessageWriter;
import com.shimizukenta.http.HttpRequestMessageInformation;
import com.shimizukenta.http.HttpServerConnectionValue;
import com.shimizukenta.http.HttpVersion1p1ResponseMessageBuilder;
import com.shimizukenta.http.HttpWriteException;

public class ApiServerService extends AbstractHttpServerService {
	
	private final ApiServerServiceConfig config;
	private final HttpVersion1p1ResponseMessageBuilder msgBuilder;
	
	public ApiServerService(ApiServerServiceConfig config) {
		super();
		this.config = config;
		this.msgBuilder = new HttpVersion1p1ResponseMessageBuilder(config.builderConfig());
	}

	@Override
	public boolean accept(HttpRequestMessageInformation requestInfo) {
		return requestInfo.path().equalsIgnoreCase(config.apiPath());
	}

	@Override
	public boolean tryService(
			HttpMessageWriter writer,
			HttpRequestMessageInformation requestInfo,
			HttpServerConnectionValue connectionValue)
					throws InterruptedException, HttpWriteException {
		
		
		// TODO Auto-generated method stub
		return false;
	}

}
