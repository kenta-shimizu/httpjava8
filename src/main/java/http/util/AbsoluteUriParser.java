package http.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbsoluteUriParser {
	
	private final int cacheSize = 256;
	private final Map<String, AbsoluteUriParseResult> cacheMap = new HashMap<>();
	
	private AbsoluteUriParser() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final AbsoluteUriParser inst = new AbsoluteUriParser();
	}
	
	public static AbsoluteUriParser getInstance() {
		return SingletonHolder.inst;
	}
	
	public AbsoluteUriParseResult parse(CharSequence absoluteUri) throws AbsoluteUriParseException {
		
		synchronized ( this ) {
			
			String s = absoluteUri.toString();
			
			AbsoluteUriParseResult r = cacheMap.get(s);
			
			if ( r == null ) {
				
				if ( cacheMap.size() > cacheSize ) {
					cacheMap.clear();
				}
				
				AbsoluteUriParseResult rr = createResult(s);
				
				cacheMap.put(s, rr);
				
				return rr;
				
			} else {
				
				return r;
			}
		}
	}
	
	private static final int defaultHttpPort = 80;
	private static final String defaultRequestLineUri = "/";
	
	private static final String GroupProtocol = "g1";
	private static final String GroupServer = "g2";
	private static final String GroupPort = "g3";
	private static final String GroupRequestLineUri = "g4";
	private static final String GroupHeaderHost = "g5";
	
	private static final String regex = "^(?<" + GroupProtocol + ">http|https)://(?<" + GroupHeaderHost + ">(?<" + GroupServer + ">[\\w-]+(\\.[\\w-]+)*)(:(?<" + GroupPort + ">[1-9][0-9]{0,4}))?)(?<" + GroupRequestLineUri + ">(/[\\w-./?%&=]*)?)$";
	private static final Pattern pattern = Pattern.compile(regex);
	
	private AbsoluteUriParseResult createResult(String absoluteUri) throws AbsoluteUriParseException {
		
		Matcher m = pattern.matcher(absoluteUri);
		
		if ( m.matches() ) {
			
			int port = defaultHttpPort;
			
			String serverPort = m.group(GroupPort);
			if ( serverPort != null ) {
				port = Integer.parseInt(serverPort, 10);
			}
			
			if ( port > 0 && port < 65536 ) {
				
				String reqLineUri = m.group(GroupRequestLineUri);
				if ( reqLineUri.isEmpty() ) {
					reqLineUri = defaultRequestLineUri;
				}
				
				return new AbsoluteUriParseResult(
						absoluteUri
						, m.group(GroupProtocol)
						, m.group(GroupServer)
						, port
						, reqLineUri
						, m.group(GroupHeaderHost));
				
			} else {
				
				throw new AbsoluteUriParseException("port is not in [1 - 65535]");
			}
			
		} else {
			
			throw new AbsoluteUriParseException();
		}
	}

}
