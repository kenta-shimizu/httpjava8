package http.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {

	private QueryParser() {
		/* Nothing */
	}
	
	private static class SingletonHolder {
		private static final QueryParser inst = new QueryParser();
		private static final QueryParseResult empty = new QueryParseResult(Collections.emptyMap());
	}
	
	public static QueryParser getInstance() {
		return SingletonHolder.inst;
	}
	
	public QueryParseResult parse(CharSequence query) {
		
		Map<String, String> map = new HashMap<>();
		
		String[] vv = query.toString().split("&");
		
		for ( String v : vv ) {
			
			if ( v.isEmpty() ) {
				continue;
			}
			
			String[] ss = v.split("=");
			
			if ( ss[0].isEmpty() ) {
				continue;
			}
			
			if ( ss.length > 1 ) {
				
				try {
					map.put(ss[0], URLDecoder.decode(ss[1], StandardCharsets.UTF_8.name()));
				}
				catch (UnsupportedEncodingException giveup) {
				}
				
			} else {
				
				map.put(ss[0], "");
			}
		}
		
		return new QueryParseResult(map);
	}
	
	public QueryParseResult empty() {
		return SingletonHolder.empty;
	}
}
