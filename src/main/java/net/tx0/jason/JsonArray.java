package net.tx0.jason;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A json array.
 *
 * <p>
 *     The array stores all values as instances of {@link JsonValue}.
 * </p>
 * <p>
 *     Typed getters and putters exist to transfer to wrap/unwrap java values.
 *     The typed getters (i.e. {@link #getString(String)} return null if the
 *     member does not exist, but throw {@link JsonException}s on type clashes,
 *     i.e. calling getString on a number value.
 * </p>
 */
public class JsonArray extends JsonValue implements List<JsonValue> {

	private final List<JsonValue> list;

	public JsonArray() {
		this( new ArrayList<JsonValue>() );
	}

	JsonArray(List<JsonValue> list) {
		this.list = list;
	}

	@Override
	void write(JsonWriter writer) {
		writer.writeBeginArray();
		for ( JsonValue value : list ) {
			if ( value == null ) {
				writer.writeNull();
			} else {
				value.write(writer);
			}
		}
		writer.writeEndArray();
	}

	public JsonObject getObject( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asObject();
	}

	public JsonArray getArray( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asArray();
	}

	public boolean getBoolean( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asBoolean();
	}

	public long getLong( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asLong();
	}

	public double getDouble( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asDouble();
	}

	public String getString( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asString();
	}

	public Instant getInstant( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asInstant();
	}

	public Duration getDuration( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asDuration();
	}

	public Number getNumber( int index ) {
		JsonValue v = get(index);
		return v == null ? null : v.asNumber();
	}

	@Override
	public void add(int index, JsonValue element) {
		list.add( index, element );
	}

	public boolean add(int value) { return add( create(value) ); }
	public boolean add(long value) { return add( create(value) ); }
	public boolean add(double value) { return add( create(value) ); }
	public boolean add( BigDecimal value) { return add( create(value) ); }
	public boolean add(Number value) { return add( create(value) ); }
	public boolean add(String value) { return add( create(value) ); }
	public boolean add(boolean value) { return add( create(value) ); }
	public boolean add( Instant value) { return add( create(value) ); }
	public boolean add( Duration value) { return add( create(value) ); }

	@Override
	public boolean add(JsonValue e) {
		return list.add( e );
	}

	@Override
	public boolean addAll(Collection<? extends JsonValue> c) {
		return list.addAll( c );
	}

	@Override
	public boolean addAll(int index, Collection<? extends JsonValue> c) {
		return list.addAll( index, c );
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains( o );
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll( c );
	}

	@Override
	public boolean equals(Object o) {
		return list.equals( o );
	}

	@Override
	public JsonValue get(int index) {
		return list.get( index );
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf( o );
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<JsonValue> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf( o );
	}

	@Override
	public ListIterator<JsonValue> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<JsonValue> listIterator(int index) {
		return list.listIterator( index );
	}

	@Override
	public JsonValue remove(int index) {
		return list.remove( index );
	}

	@Override
	public boolean remove(Object o) {
		return list.remove( o );
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll( c );
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll( c );
	}

	@Override
	public JsonValue set(int index, JsonValue element) {
		return list.set( index, element );
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<JsonValue> subList(int fromIndex, int toIndex) {
		return list.subList( fromIndex, toIndex );
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray( a );
	}

	@Override
	public JsonValueType getType() {
		return JsonValueType.ARRAY;
	}

	@Override
	public JsonArray asArray() {
		return this;
	}
}
