package example2;

import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.http.AbstractHttpServerService;
import com.shimizukenta.http.HttpMessageBody;
import com.shimizukenta.http.HttpMessageHeader;
import com.shimizukenta.http.HttpMessageHeaderField;
import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageWriter;
import com.shimizukenta.http.HttpRequestMessageInformation;
import com.shimizukenta.http.HttpResponseMessage;
import com.shimizukenta.http.HttpServerConnectionValue;
import com.shimizukenta.http.HttpStatus;
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
		
		HttpMessageBody body = createHtml(requestInfo);
		
		final List<HttpMessageHeader> headers = new ArrayList<>();
		
		config.builderConfig().serverName()
		.ifPresent(serverName -> {
			headers.add(HttpMessageHeader.create(
					HttpMessageHeaderField.Server,
					serverName));
		});
		
		headers.add(HttpMessageHeader.create(
				HttpMessageHeaderField.ContentType,
				"text/html"));
		
		headers.add(HttpMessageHeader.create(
				HttpMessageHeaderField.ContentLength,
				String.valueOf(body.length())));
		
		headers.add(HttpMessageHeader.create(
				HttpMessageHeaderField.Connection,
				"close"));
		
		
		HttpResponseMessage rsp = msgBuilder.build(
				HttpStatus.OK,
				HttpMessageHeaderGroup.create(headers),
				body);
		
		this.write(writer, rsp, requestInfo);
		return rsp.isKeepAlive();
	}
	
	private HttpMessageBody createHtml(HttpRequestMessageInformation reqInfo) {
		
		StringBuilder html = new StringBuilder();
		
		html.append("<html><head><title>Example2</title></head><body><h1>Example2</h1><hr />");
		
		reqInfo.query().forEach((key, v) -> {
			html.append("<p>")
			.append(key)
			.append(": ")
			.append(v)
			.append("</p>");
		});
		
		html.append("</body></html>");
		
		return HttpMessageBody.fromText(html);
	}

}
