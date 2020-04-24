package com.shimizukenta.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class HttpQuery {
	
	private static class Inner {
		private final String key;
		private final String value;
		private Inner(String k, String v) {
			this.key = k;
			this.value = v;
		}
	}
	
	private final Collection<Inner> vv = new ArrayList<>();
	
	private HttpQuery() {
		/* Nothing */
	}
	
	public String get(CharSequence key) {
		final String s = key.toString();
		return vv.stream()
				.filter(v -> v.key.equals(s))
				.findFirst()
				.map(v -> v.value)
				.orElse(null);
	}
	
	public String getOrDefault(CharSequence key, CharSequence defaultValue) {
		Objects.requireNonNull(defaultValue);
		final String s = key.toString();
		return vv.stream()
				.filter(v -> v.key.equals(s))
				.findFirst()
				.map(v -> v.value)
				.orElse(defaultValue.toString());
	}
	
	public boolean containsKey(CharSequence key) {
		return get(key) != null;
	}
	
	public Set<String> keys() {
		return vv.stream()
				.map(v -> v.key)
				.collect(Collectors.toSet());
	}
	
	public void forEach(BiConsumer<String, String> c) {
		vv.forEach(v -> {
			c.accept(v.key, v.value);
		});
	}
	
	private static final HttpQuery empty = new HttpQuery();
	
	public static HttpQuery parse(CharSequence query) {
		return _parse(query.toString());
	}
	
	public static HttpQuery parse(HttpRequestMessage request) {
		
		switch ( request.requestLine().method() ) {
		case HEAD:
		case GET: {
			
			String uri = request.requestLine().uri();
			
			String[] ss = uri.split("\\?", 2);
			
			if ( ss.length == 2 ) {
				
				return _parse(ss[1]);
				
			} else {
				
				return empty;
			}
			/* break; */
		}
		case POST: {
			
			String query = new String(request.body().getBytes(), StandardCharsets.UTF_8);
			return _parse(query);
			/* break; */
		}
		default: {
			
			return empty;
		}
		}
	}
	
	private static HttpQuery _parse(String query) {
		
		final HttpQuery inst = new HttpQuery();
		
		String[] ss = query.split("&");
		
		for ( String s : ss ) {
			
			String[] kv = s.split("=", 2);
			
			if ( ! kv[0].isEmpty() ) {
				
				if ( kv.length == 2 ) {
					
					try {
						String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name());
						inst.vv.add(new Inner(kv[0], v));
					}
					catch ( UnsupportedEncodingException giveup ) {
					}
					
				} else {
					
					inst.vv.add(new Inner(kv[0], ""));
				}
			}
		}
		
		return inst;
	}
}
