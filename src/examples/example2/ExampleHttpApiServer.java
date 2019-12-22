package example2;

import java.io.IOException;

import http.api.HttpApiServer;

public class ExampleHttpApiServer extends HttpApiServer {

	public ExampleHttpApiServer(ExampleHttpApiServerConfig config) {
		super(config);
		
		this.addApiServerService(new ExampleHttpApiServerService(config.apiServerServiceConfig()));
	}
	
	public static ExampleHttpApiServer open(ExampleHttpApiServerConfig config) throws IOException {
		
		ExampleHttpApiServer inst = new ExampleHttpApiServer(config);
		
		try {
			inst.open();
		}
		catch ( IOException e ) {
			
			try {
				inst.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		
		return inst;
	}
	
}
