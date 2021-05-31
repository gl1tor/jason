package net.tx0.jason;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class JsonValue {

	public static JsonArray create( Object... values ) {
		Deque<Object> check = new LinkedList<>();
		return createFromIterable( check, Arrays.asList(values) );
	}

	public static JsonArray create( Iterable<Object> values ) {
		Deque<Object> check = new LinkedList<>();
		return createFromIterable( check, values );
	}

	public static JsonObject create( Map<String,Object> members ) {
		Deque<Object> check = new LinkedList<>();
		return createFromMap( check, members );
	}

	private static JsonValue createFromObject( Deque<Object> check, Object in ) {
		if ( check.contains( in ) )
			throw new JsonException( "cyclic object graph" );
		if ( in instanceof JsonValue ) {
			return (JsonValue) in;
		} else if ( in instanceof Number ) {
			return create((Number) in);
		} else if ( in instanceof String ) {
			return create( (String)in );
		} else if ( in instanceof Boolean ) {
			return create( (Boolean)in );
		} else if ( in instanceof Instant ) {
			return create((Instant) in);
		} else if ( in instanceof Map ) {
			// silently assumes the map uses strings as key
			return createFromMap( check, (Map<String, Object>) in);
		} else if ( in instanceof Iterable ) {
			return createFromIterable( check, (Iterable<Object>) in);
		} else {
			throw new IllegalArgumentException( "Unsupported type" );
		}
	}

	private static JsonArray createFromIterable( Deque<Object> check, Iterable<Object> in ) {
		if ( check.contains( in ) )
			throw new IllegalArgumentException( "cyclic object graph" );
		ArrayList<JsonValue> list = new ArrayList<>();
		for ( Object object : in ) {
			list.add( createFromObject(check, object) );
		}
		return new JsonArray(list);
	}

	private static JsonObject createFromMap( Deque<Object> check, Map<String, Object> in ) {
		if ( check.contains( in ) )
			throw new IllegalArgumentException( "cyclic object graph" );
		Map<String,JsonValue> map = new HashMap<>();
		check.push( in );
		for ( Map.Entry<String, Object> entry : in.entrySet() ) {
			map.put( entry.getKey(), create( entry.getValue() ) );
		}
		check.pop();
		return new JsonObject(map);
	}

	public static JsonNumber create( Number number ) {
		return JsonNumber.createNumber( number );
	}

	public static JsonScalar create( String value ) {
		if ( value == null )
			throw new IllegalArgumentException("String value is null");
		return new JsonString( value );
	}

	public static JsonScalar create( Instant instant ) {
		return create( DateTimeFormatter.ISO_INSTANT.format( instant ) );
	}

	public static JsonScalar create( Duration duration ) {
		return create( duration.toString() );
	}

	public static JsonScalar create( boolean value ) {
		return value ? JsonBoolean.TRUE : JsonBoolean.FALSE;
	}

	public static JsonScalar create( int value ) {
		return JsonNumber.createNumber( value );
	}

	public static JsonScalar create( long value ) {
		return JsonNumber.createNumber( value );
	}

	public static JsonScalar create( BigDecimal value ) {
		return JsonNumber.createNumber( value );
	}

	public static JsonScalar create( double value ) {
		return JsonNumber.createNumber( value );
	}

	/**
	 * The type of the value being represented.
	 * 
	 * @return the type of the value or null if this is a json 'null' value.
	 */
	public abstract JsonValueType getType();
	
	abstract void write( JsonWriter writer );

	/**
	 * Java boolean value.
	 *  
	 * @return the primitive value.
	 * @throws JsonException if this value is not representing a boolean.
	 */
	public boolean asBoolean() { throw new JsonException( "Not a boolean value" ); }

	/**
	 * This as a java {@link Number}.
	 *
	 * @return the number value.
	 * @throws JsonException if this value is not a number.
	 */
	public Number asNumber() { throw new JsonException( "Not a numeric value" ); }

	/**
	 * This as a java integer.
	 *
	 * Relies on java number conversion and does not throw {@link NullPointerException}s.
	 *
	 * @return the integer value.
	 * @throws JsonException if this value is not a number.
	 */
	public int asInteger() {
		return asNumber().intValue();
	}

	/**
	 * This as a java long.
     *
	 * Relies on java number conversion and does not throw {@link NullPointerException}s.
	 *
	 * @return the long value.
	 * @throws JsonException if this value is not a number.
	 */
	public long asLong() {
		return asNumber().longValue();
	}

	/**
	 * This as a java double.
	 *
	 * Relies on java number conversion and does not throw {@link NullPointerException}s.
	 *
	 * @return the double value.
	 * @throws JsonException if this value is not a number.
	 */
	public double asDouble() {
		return asNumber().doubleValue();
	}

	/**
	 * This as a java double.
	 *
	 * Relies on java number conversion and does not throw {@link NullPointerException}s.
	 *
	 * @return the double value.
	 * @throws JsonException if this value is not a number.
	 */
	public BigDecimal asBigDecimal() {
		Number number = asNumber();
		if ( number instanceof BigDecimal )
			return (BigDecimal) number;
		return new BigDecimal(asDouble());
	}

	public Instant asInstant() { throw new JsonException( "Not an instant" ); }

	public Duration asDuration() { throw new JsonException( "Not a duration" ); }

	/**
	 * Java string value.
	 *  
	 * @return the string value or null if this is a json 'null'.
	 * @throws JsonException if this value is not representing a string.
	 */
	public String asString() { throw new JsonException( "Not a string value" ); }

	/**
	 * This as a json object.
	 *  
	 * @return the object value or null if this is a json 'null'
	 * @throws JsonException if this value is not representing an object.
	 */
	public JsonObject asObject() { throw new JsonException( "Not a json object" ); }

	/**
	 * This as a json array.
	 *
	 * @return the array value or null if this is a json 'null'
	 * @throws JsonException if this value is not representing an array.
	 */
	public JsonArray asArray() { throw new JsonException( "Not a json array" ); }

	/**
	 * This as a json array.
	 *
	 * @return the scalar value or null if this is a json 'null'
	 * @throws JsonException if this value is not representing an array.
	 */
	public JsonScalar asScalar() { throw new JsonException( "Not a json scalar" ); }


	/**
	 * Navigate from this value to one of it's descendants by interpreting the supplied path.
	 *
	 * The following are valid path expressions:
	 * <ul>
	 *     <li>"" returns this value</li>
	 *     <li>"[i]" returns the i-th element if this is an array</li>
	 *     <li>".abc" returns the value whose key is <em>ebc</em> if this is an object, the first dot is optional</li>
	 * </ul>
	 *
	 * The array and object select operators can be applied multiple times by concatenating the expressions.
	 *
	 * Calling find on <pre>{ "a" : [ { "b": "c" }, { "b": "d" } }</pre> with <pre>"a[0].b"</pre> yields "c".
	 *
	 * If any of the intermediate nodes don't exist, null is returned.
	 *
	 * @param path the path to interpret
	 * @return the json value if found, null otherwise
	 */
	public final JsonValue find( String path ) {
		return findIn( this, path );
	}

	private static int findExprNext( String path, int index ) {
		for ( int len = path.length(); index < len; ++index ) {
			switch ( path.charAt(index) ) {
				case '[':
				case '.':
					return index;
			}
		}
		return index;
	}

	private static JsonValue findIn( JsonValue node, String path ) {

		int l = path.length();

		for ( int i = 0, j = findExprNext( path, i+1 ); i < l && node != null; i = j, j = findExprNext( path, j + 1 ) ) {

			char c = path.charAt(i);

			if ( c == '.' ) {

				if ( node.getType() != JsonValueType.OBJECT )
					return null;

				node = node.asObject().get( path.substring( i + 1, j ) );

			} else if ( c == '[' ) {

				if ( node.getType() != JsonValueType.ARRAY )
					return null;

				int k = path.indexOf(']', i);

				if ( k != j - 1 )
					throw new IllegalArgumentException("Closing bracket expected k=" + k + " j=" + j);

				node = node.asArray().get(Integer.parseInt(path.substring(i + 1, k)));

			} else if ( i == 0 ) {

				if ( node.getType() != JsonValueType.OBJECT )
					return null;

				node = node.asObject().get( path.substring( 0, j ) );

			} else {

				throw new IllegalArgumentException( "Not a valid expression at index " +i );

			}

		}

		return node;

	}

}
