package test;

import java.util.ArrayList;
import java.util.List;

import com.shimizukenta.http.HttpKeepAliveValue;
import com.shimizukenta.http.HttpMessageBody;
import com.shimizukenta.http.HttpMessageHeader;
import com.shimizukenta.http.HttpMessageHeaderField;
import com.shimizukenta.http.HttpMessageHeaderGroup;
import com.shimizukenta.http.HttpMessageRequestLine;
import com.shimizukenta.http.HttpMethod;
import com.shimizukenta.http.HttpRequestMessage;
import com.shimizukenta.http.HttpResponseMessage;
import com.shimizukenta.http.HttpResponseMessageBuilder;
import com.shimizukenta.http.HttpServerConnectionValue;
import com.shimizukenta.http.HttpVersion;
import com.shimizukenta.http.HttpVersion1p1ResponseMessageBuilder;
import com.shimizukenta.http.HttpVersion1p1ResponseMessageBuilderConfig;

public class Test1 {

	public Test1() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		echo("test-begin");
		
		HttpMessageRequestLine requestLine = HttpMessageRequestLine.create(
				HttpMethod.GET,
				"/",
				HttpVersion.HTTP_1_1);
		
		List<HttpMessageHeader> headers = new ArrayList<>();
		
		headers.add(HttpMessageHeader.create(
				HttpMessageHeaderField.Host,
				"localhost"));
		
		headers.add(HttpMessageHeader.create(
				HttpMessageHeaderField.AcceptEncoding,
				"gzip;q=0.9, deflate;q=0.8"));
		
		headers.add(HttpMessageHeader.create(
				HttpMessageHeaderField.Origin,
				"localhost"));
		
		HttpRequestMessage reqMsg = new HttpRequestMessage(
				requestLine,
				HttpMessageHeaderGroup.create(headers),
				HttpMessageBody.empty());
		
		echo(reqMsg);
		
		final HttpVersion1p1ResponseMessageBuilderConfig builderConfig = new HttpVersion1p1ResponseMessageBuilderConfig();
		builderConfig.serverName("TEST-SERVER");
		
		final HttpResponseMessageBuilder builder = new HttpVersion1p1ResponseMessageBuilder(builderConfig);
		
		final HttpServerConnectionValue connectionValue = new HttpServerConnectionValue(
				null,
				new HttpKeepAliveValue(5L, 100));
		
		HttpResponseMessage rspMsg = builder.fromXml(reqMsg, connectionValue, "<>");
		
		echo(rspMsg);
		
		echo("isKeepAlive: " + rspMsg.isKeepAlive());
		
		echo("test-end");
	}
	
	private static final Object syncEcho = new Object();
	
	private static void echo(Object o) {
		
		synchronized ( syncEcho ) {
			if ( o instanceof Throwable ) {
				((Throwable) o).printStackTrace();
			} else {
				System.out.println(o);
			}
			System.out.println();
		}
	}

}
