package com.shimizukenta.http;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HttpQualityParameter {
	
	private static final float QUALITY_MAX = 1.0F;
	private static final float QUALITY_MIN = 0.0F;
	
	private static class Inner implements Comparable<Inner>{
		
		private final String value;
		private final Float quality;
		
		private Inner(String v, float q) {
			this.value = v;
			this.quality = Float.valueOf(q);
		}

		@Override
		public int compareTo(Inner other) {
			return quality.compareTo(other.quality);
		}
	}
	
	private final List<Inner> vv = new ArrayList<>();
	
	private HttpQualityParameter() {
		/* No-thing */
	}
	
	/**
	 * get Quality Desc Sorted Parameters<br />
	 * 
	 * @return parameters sorted by desc quality
	 */
	public List<String> parameters() {
		return vv.stream()
				.sorted(Comparator.reverseOrder())
				.map(v -> v.value)
				.collect(Collectors.toList());
	}
	
	public boolean contains(CharSequence value) {
		String s = value.toString();
		return vv.stream()
				.map(v -> v.value)
				.anyMatch(v -> v.equalsIgnoreCase(s));
	}
	
	public static HttpQualityParameter parse(CharSequence cs) {
		
		final HttpQualityParameter inst = new HttpQualityParameter();
		
		String[] ss = cs.toString().split(",");
		
		for ( String s : ss ) {
			
			String[] pp = s.trim().split(";");
			
			String p = pp[0].trim();
			
			if ( ! p.isEmpty() ) {
				inst.vv.add(new Inner(p, getQuality(pp)));
			}
		}
		
		return inst;
	}
	
	private static float getQuality(String[] pp) {
		
		for ( int i = 1, m = pp.length; i < m; ++i ) {
			
			String p = pp[i].trim();
			
			if ( p.startsWith("q=") ) {
				
				try {
					float v = Float.parseFloat(p.substring(2).trim());
					
					if ( v >= QUALITY_MIN && v <= QUALITY_MAX ) {
						
						return v;
						
					} else {
						
						return QUALITY_MIN;
					}
				}
				catch ( NumberFormatException fail_request ) {
					return QUALITY_MIN;
				}
			}
		}
		
		return QUALITY_MAX;
	}
	
}
