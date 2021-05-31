package net.tx0.jason;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A json object.
 *
 * <p>
 *     The object stores all member values as instances of {@link JsonValue}.
 * </p>
 * <p>
 *     Typed getters and putters exist to transfer to wrap/unwrap java values.
 *     The typed getters (i.e. {@link #getString(String)} return null if the
 *     member does not exist, but throw {@link JsonException}s on type clashes,
 *     i.e. calling getString on a number value.
 * </p>
 */
public class JsonObject extends JsonValue implements Map<String, JsonValue> {

    public static class JsonMember {
        private final String name;
        private final JsonValue value;

        public JsonMember( String name, JsonValue value ) {
            this.name = name;
            this.value = value;
        }
    }

    public static JsonObject create( JsonMember... members ) {
        JsonObject object = new JsonObject();
        for ( int i = 0; i < members.length; ++i )
            object.put(members[i].name, members[i].value);
        return object;
    }

    private final Map<String, JsonValue> map;

    public JsonObject() {
        this(new HashMap<String, JsonValue>());
    }

    JsonObject( Map<String, JsonValue> map ) {
        this.map = map;
    }

    /**
     * Renames a member if it exists.
     */
    public void rename( String oldKey, String newKey ) {
        if ( containsKey(oldKey) ) {
            JsonValue value = get(oldKey);
            put(newKey, value);
            remove(oldKey);
        }
    }

    @Override
    void write( JsonWriter writer ) {
        writer.writeStartObject();
        for ( Entry<String, JsonValue> entry : map.entrySet() ) {
            writer.writeMember(entry.getKey());
            JsonValue value = entry.getValue();
            if ( value == null ) {
                writer.writeNull();
            } else {
                value.write(writer);
            }
        }
        writer.writeEndObject();
    }

    public JsonObject getObject( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asObject();
    }

    public JsonArray getArray( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asArray();
    }

    public Boolean getBoolean( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asBoolean();
    }

    public Long getLong( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asLong();
    }

    public Integer getInteger( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asInteger();
    }

    public Double getDouble( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asDouble();
    }

    public Number getNumber( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asNumber();
    }

    public String getString( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asString();
    }

    public Instant getInstant( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asInstant();
    }

    public Duration getDuration( String memberName ) {
        JsonValue v = get(memberName);
        return v == null ? null : v.asDuration();
    }

    public JsonValue put( String key, boolean value ) {
        return put(key, create(value));
    }

    public JsonValue put( String key, long value ) {
        return put(key, create(value));
    }

    public JsonValue put( String key, double value ) {
        return put(key, create(value));
    }

    public JsonValue put( String key, BigDecimal value ) {
        return put(key, create(value));
    }

    public JsonValue put( String key, String value ) {
        return put(key, create(value));
    }

    public JsonValue put( String key, Instant value ) {
        return put(key, create(value));
    }

    public JsonValue put( String key, Duration value ) {
        return put(key, create(value));
    }

    public JsonValueType getTypeOf( Object memberName ) {
        JsonValue value = map.get(memberName);
        return value != null ? value.getType() : null;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey( Object key ) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue( Object value ) {
        return map.containsValue(value);
    }

    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals( Object o ) {
        return map.equals(o);
    }

    @Override
    public JsonValue get( Object key ) {
        return map.get(key);
    }

    @Override
    public JsonValueType getType() {
        return JsonValueType.OBJECT;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public JsonValue put( String key, JsonValue value ) {
        return map.put(key, value);
    }

    @Override
    public void putAll( Map<? extends String, ? extends JsonValue> m ) {
        map.putAll(m);
    }

    @Override
    public JsonValue remove( Object key ) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<JsonValue> values() {
        return map.values();
    }

    @Override
    public JsonObject asObject() {
        return this;
    }
}
