package example2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import http.util.HttpServer;

public class ExampleHttpApiServer {

	public ExampleHttpApiServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		String hostName = "HTTP-API-SERVER";
		
		HttpApiServerConfig config = new HttpApiServerConfig();
		
		config.serverAddress(new InetSocketAddress("127.0.0.1", 80));
		
		config.generalFileServerServiceConfig().serverRoot(Paths.get("/path/to/root-directory"));
		config.generalFileServerServiceConfig().directoryIndex("index.html");
		config.generalFileServerServiceConfig().hostName(hostName);
		
		config.apiServerServiceConfig().absolutePath("/api");
		config.apiServerServiceConfig().hostName(hostName);
		
		try (
				HttpServer server = HttpApiServer.open(config);
				) {
			
			server.addAccessLogListener(System.out::println);
			server.addResponseLogListener(System.out::println);
			server.addLogListener(System.out::println);
			
			synchronized ( ExampleHttpApiServer.class ) {
				ExampleHttpApiServer.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

	}
	
}
