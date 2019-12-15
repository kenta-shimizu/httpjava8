package http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class HttpLog {
	
	private static final String BR = System.lineSeparator();
	private static final String SPACE = "\t";

	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private final LocalDateTime timestamp;
	private final String subject;
	private final Object value;
	
	private String parsedString;
	
	public HttpLog(CharSequence subject, LocalDateTime timestamp, Object value) {
		this.subject = subject.toString();
		this.timestamp = timestamp;
		this.value = value;
		
		this.parsedString = null;
	}
	
	public HttpLog(CharSequence subject, Object value) {
		this(subject, LocalDateTime.now(), value);
	}
	
	public HttpLog(CharSequence subject) {
		this(subject, LocalDateTime.now(), null);
	}
	
	public HttpLog(Throwable t) {
		this(Objects.requireNonNull(t).getClass().getName(), LocalDateTime.now(), t);
	}
	
	public String subject() {
		return this.subject;
	}
	
	public LocalDateTime timestamp() {
		return this.timestamp;
	}
	
	public Optional<Object> value() {
		return value == null ? Optional.empty() : Optional.of(value);
	}
	
	@Override
	public String toString() {
		
		synchronized ( this ) {
			
			if ( parsedString == null ) {
				
				StringBuilder sb = new StringBuilder()
						.append(toStringTimestamp())
						.append(SPACE)
						.append(subject());
	
				String v = toStringValue();
				if ( ! v.isEmpty() ) {
					sb.append(BR).append(v);
				}
				
				parsedString = sb.toString();
			}
			
			return parsedString;
		}
	}
	
	protected String toStringTimestamp() {
		return timestamp().format(DATETIME);
	}
	
	protected String toStringValue() {
		
		return value().map(o -> {
			
			if ( o instanceof Throwable ) {
				
				try (
						StringWriter sw = new StringWriter();
						) {
					
					try (
							PrintWriter pw = new PrintWriter(sw);
							) {
						
						((Throwable) o).printStackTrace(pw);
						pw.flush();
						
						return sw.toString();
					}
				}
				catch ( IOException e ) {
					return e.toString();
				}
				
			} else {
				
				return o.toString();
			}
			
		})
		.orElse("");
	}

}
