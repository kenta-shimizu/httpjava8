package example2;

import java.util.Objects;

import com.shimizukenta.http.HttpVersion1p1ResponseMessageBuilderConfig;

public class ApiServerServiceConfig {
	
	private final HttpVersion1p1ResponseMessageBuilderConfig builderConfig = new HttpVersion1p1ResponseMessageBuilderConfig();
	private String apiPath;
	
	public ApiServerServiceConfig() {
		apiPath = "";
	}
	
	public String apiPath() {
		synchronized ( this ) {
			return this.apiPath;
		}
	}
	
	public void apiPath(CharSequence path) {
		synchronized ( this ) {
			this.apiPath = Objects.requireNonNull(path, "apiPath require non-null").toString();
		}
	}
	
	public HttpVersion1p1ResponseMessageBuilderConfig builderConfig() {
		return this.builderConfig;
	}
	
	public void serverName(CharSequence serverName) {
		this.builderConfig.serverName(serverName);
	}
	
}
