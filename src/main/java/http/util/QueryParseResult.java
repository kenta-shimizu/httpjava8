package http.util;

import java.util.Map;
import java.util.Set;

public class QueryParseResult {
	
	private final Map<String, String> map;
	
	protected QueryParseResult(Map<String, String> map) {
		this.map = map;
	}
	
	public Set<String> keys() {
		return map.keySet();
	}
	
	public String get(CharSequence key) {
		return map.get(key.toString());
	}
}
