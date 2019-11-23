# httpjava8

## Introduction

This library is template of Http Server that runs only on Java8+.  
Use to create simple API HTTP Server.


## for HTTP Server

### Open General HTTP Server

```
    HttpServerConfig config = new HttpServerConfig();
    
    config.serverAddress(new InetSocketAddress("127.0.0.1", 80));

    config.generalFileServerServiceConfig().serverRoot(Paths.get("/path/to/root-directory"));
    config.generalFileServerServiceConfig().directoryIndex("index.html");
    config.generalFileServerServiceConfig().hostName("GENERAL-SERVER");

    HttpServer server = HttpServer.open(config);
```

see also "/src/examples/example1/ExampleHttpGeneralServer.java"



### Create API HTTP Server

Override "HttpServer#writeResponseMessage"  
see "/src/examples/example2/ExampleHttpApiServer.java"


## for HTTP  Client

Use "java.net.http.HttpClient" on Java11+ or other library.

