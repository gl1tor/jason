package net.tx0.jason;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class JsonString extends JsonScalar {

	private final String value;

	JsonString(String value) {
		this.value = value;
	}

	@Override
	public JsonValueType getType() {
		return JsonValueType.STRING;
	}

	@Override
	void write(JsonWriter writer) {
		writer.write(value);
	}

	@Override
	public Instant asInstant() {
		return Instant.from( DateTimeFormatter.ISO_INSTANT.parse( value ) );
	}

	@Override
	public Duration asDuration() {
		return Duration.parse( value );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		Number nbr;

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( !( obj instanceof JsonString ) )
			return false;
		JsonString other = (JsonString) obj;
		if ( value == null ) {
			if ( other.value != null )
				return false;
		} else if ( !value.equals( other.value) )
			return false;
		return true;
	}

	@Override
	public String asString() {
		return value;
	}
	
}
