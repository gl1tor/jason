package net.tx0.jason;

import java.io.IOException;

public class JsonException extends RuntimeException {

	static JsonException wrap( IOException e ) {
		return new JsonIOException(e);
	}

	JsonException(String message) {
		super( message );
	}

	JsonException( String message, Throwable cause ) {
		super(message, cause);
	}

	protected JsonException( Throwable cause ) {
		super( cause );
	}

}
