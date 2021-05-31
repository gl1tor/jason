package net.tx0.jason;

import java.util.*;

/**
 * Constructs an object model read from a tag stream.
 */
abstract class JsonParser {

	private JsonParser() {}

	static JsonValue parseText( JsonReader reader ) {
		
		JsonValue value;

		if ( !reader.hasNext() )
			throw new JsonException( "Premature end of file" );

		JsonToken kind = reader.next();
		switch ( kind ) {
			case BEGIN_ARRAY:
				value = parseArray( reader );
				break;
			case BEGIN_OBJECT:
				value = parseObject( reader );
				break;
			default:
				value = parseValue( reader, kind );
				break;
		}

		return value;
	}

	private static JsonValue parseValue( JsonReader reader, JsonToken kind ) {
		switch ( kind ) {
			case BOOLEAN:
				return reader.getBooleanValue() ? JsonBoolean.TRUE : JsonBoolean.FALSE;
			case STRING:
				return new JsonString( reader.getStringValue() );
			case NUMBER:
				return JsonNumber.createNumber(reader.getNumberValue());
			case NULL:
				return null;
			case BEGIN_ARRAY:
				return parseArray( reader );
			case BEGIN_OBJECT:
				return parseObject( reader );
		}

		throw new JsonException( "Unexpected token " + kind + " while parsing value" );
	}

	static JsonObject parseObject( JsonReader reader ) {
		JsonToken tag;
		Map<String, JsonValue> members;
		String key;
		
		members = new LinkedHashMap<String, JsonValue>();
		
		tag = reader.next();
		
		while ( tag == JsonToken.MEMBER_NAME ) {
			
			key = reader.getMemberName();
			
			members.put( key, parseValue( reader, reader.next() ) );
			
			tag = reader.next();

		}

		if ( tag != JsonToken.END_OBJECT ) {
			throw new JsonException( "Expected end of object" );
		}

		return new JsonObject( members );
	}

	static JsonArray parseArray( JsonReader reader ) {
		JsonToken tag;
		List<JsonValue> elements = new ArrayList<>();
		
		tag = reader.next();
		
		while ( tag != JsonToken.END_ARRAY ) {
			elements.add(parseValue(reader, tag));
			tag = reader.next();
		}
		
		return new JsonArray( elements );
	}

}
