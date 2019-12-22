package example2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import http.util.HttpServer;

public class Example2 {

	public Example2() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		String hostName = "HTTP-API-SERVER";
		
		ExampleHttpApiServerConfig config = new ExampleHttpApiServerConfig();
		
		config.serverAddress(new InetSocketAddress("127.0.0.1", 80));
		
		config.generalFileServerServiceConfig().serverRoot(Paths.get("/path/to/root-directory"));
		config.generalFileServerServiceConfig().directoryIndex("index.html");
		config.generalFileServerServiceConfig().hostName(hostName);
		
		config.apiServerServiceConfig().absolutePath("/api");
		config.apiServerServiceConfig().hostName(hostName);
		
		try (
				HttpServer server = ExampleHttpApiServer.open(config);
				) {
			
			server.addAccessLogListener(System.out::println);
			server.addResponseLogListener(System.out::println);
			server.addLogListener(System.out::println);
			
			synchronized ( Example2.class ) {
				Example2.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

	}
	
}
