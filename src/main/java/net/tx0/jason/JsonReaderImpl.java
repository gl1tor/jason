package net.tx0.jason;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

final class JsonReaderImpl implements JsonReader {

	private enum State {
		VALUE,
		OBJECT_FIRST_MEMBER,
		OBJECT_NEXT_MEMBER,
		OBJECT_NAME_SEPARATOR,
		ARRAY_FIRST_ELEMENT,
		ARRAY_NEXT_ELEMENT,
		ARRAY_VALUE_SEPARATOR,
		MEMBER_START,
		MEMBER_VALUE,
	}

	private final JsonScanner scanner;
	private final boolean strict;
	private final boolean floatingPoint;
	private final Deque<State> states = new ArrayDeque<State>();
	private final int maximumDepth;

	private String attributeName;
	private String text;
	
	JsonReaderImpl( JsonConfig config, JsonScanner scanner) {
		this.maximumDepth = config.getMaximumDepth();
		this.floatingPoint = config.isUseFloatingPoint();
		this.strict = config.isStrict();
		this.scanner = scanner;
		this.states.push( State.VALUE );
	}

	final JsonSyntaxException error( String message ) {
		return new JsonSyntaxException( message, scanner.location() );
	}

	@Override
	public String getMemberName() {
		if ( attributeName == null )
			throw new JsonException( "No attribute" );
		return attributeName;
	}
	
	@Override
	public String getStringValue() {
		return text;
	}
	
	@Override
	public boolean getBooleanValue() {
		if ( text == "true" ) // that's == not equals
			return true;
		return false;
	}

	@Override
	public Number getNumberValue() {
		try {
			return Long.parseLong( text );
		} catch ( NumberFormatException e1 ) {
			if ( floatingPoint ) {
				try {
					return Double.parseDouble( text );
				} catch ( NumberFormatException e2 ) {
					return new BigDecimal( text );
				}
			} else {
				return new BigDecimal( text );

			}
		}
	}

	@Override
	public boolean hasNext() {
		return !states.isEmpty();
	}
	
	@Override
	public JsonToken next() {

		State state;
		JsonToken tag;

		try {

			tag = next0();

			if ( states.isEmpty() ) {
				int token = scanner.next();
				if ( JsonScanner.EOF != token )
					throw error("Expected end of file");
			}

			return tag;

		} catch ( IOException e ) {
			throw JsonException.wrap(e);
		} catch ( NoSuchElementException e ) {
			throw error( "Bad json syntax" );
		}
    		
	}

	private JsonToken next0() throws IOException {

		int token;
		State state;

		do {

			token = scanner.next();

			state = states.pop();
			switch ( state ) {

				case VALUE:
					return nextValue( token );

				case ARRAY_FIRST_ELEMENT:
					if ( token == ']' ) {
						return JsonToken.END_ARRAY;
					} else {
						states.push( State.ARRAY_VALUE_SEPARATOR );
						return nextValue( token );
					}

				case ARRAY_NEXT_ELEMENT:
					states.push( State.ARRAY_VALUE_SEPARATOR );
					return nextValue( token );

				case ARRAY_VALUE_SEPARATOR:
					if ( token == ']' ) {
						return JsonToken.END_ARRAY;
					} else if ( token == ',' ) {
						states.push( State.ARRAY_NEXT_ELEMENT );
						continue;
					} else {
						throw error( "] or , expected" );
					}

				case OBJECT_FIRST_MEMBER:
					if ( token == '}' ) {
						return JsonToken.END_OBJECT;
					} else if ( token == JsonScanner.TOKEN_STRING ) {
						attributeName = scanner.text();
						states.push( State.MEMBER_START);
						return JsonToken.MEMBER_NAME;
					} else {
						throw error( "attribute name excepted" );
					}

				case OBJECT_NEXT_MEMBER:
					if ( token == JsonScanner.TOKEN_STRING ) {
						attributeName = scanner.text();
						states.push( State.MEMBER_START);
						return JsonToken.MEMBER_NAME;
					} else {
						throw error( "attribute name excepted" );
					}

				case OBJECT_NAME_SEPARATOR:

					if ( token == '}' ) {
						return JsonToken.END_OBJECT;
					} else if ( token == ',' ) {
						states.push( State.OBJECT_NEXT_MEMBER);
						continue;
					} else {
						throw error( "attribute expected" );
					}

				case MEMBER_START:
					if ( token != ':' )
						throw error( "colon excepted" );
					states.push( State.MEMBER_VALUE);
					continue;

				case MEMBER_VALUE:
					states.push( State.OBJECT_NAME_SEPARATOR );
					return nextValue( token );
			}

			// unreachable
			throw new IllegalStateException();

		} while ( true );
	}

	private JsonToken nextValue( int token) {
		switch ( token ) {
			case JsonScanner.TOKEN_FALSE:
				text = "false";
				return JsonToken.BOOLEAN;
			case JsonScanner.TOKEN_TRUE:
				text = "true";
				return JsonToken.BOOLEAN;
			case JsonScanner.TOKEN_NULL:
				return JsonToken.NULL;
			case JsonScanner.TOKEN_INTEGER:
			case JsonScanner.TOKEN_FRACTIONAL_NUMBER:
				text = scanner.text();
				return JsonToken.NUMBER;
			case JsonScanner.TOKEN_STRING:
				text = scanner.text();
				return JsonToken.STRING;
			case '{':
				states.push( State.OBJECT_FIRST_MEMBER);
				if ( states.size() >= maximumDepth ) {
					throw new JsonException( "Maximum depth exceeded (" + states.size() + ")" );
				}
				return JsonToken.BEGIN_OBJECT;
			case '[':
				states.push( State.ARRAY_FIRST_ELEMENT );
				if ( states.size() >= maximumDepth ) {
					System.out.println( states );
					throw new JsonException( "Maximum depth exceeded (" + states.size() + ")" );
				}
				return JsonToken.BEGIN_ARRAY;
			default:
				throw new JsonException( "Expected value" );
		}
	}

}
