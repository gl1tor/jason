package net.tx0.json;

import net.tx0.jason.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {

    @Test
    public void testFactory() {
        JsonFactory factory = new JsonFactory(new JsonConfigBuilder().setNoLayout().build());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonWriter writer = factory.createWriter(outputStream);
        writer.write(123);
        writer.close();
        assertEquals( "123", new String(outputStream.toByteArray()) );
    }

    @Test
    public void testConsumeEntireInput() {
        assertThrows( JsonException.class, ()-> {
            JsonValue val = Json.deserialize(null, "true false");
        });
    }

    @Test
    public void testWhitespace() {
        JsonValue val = Json.deserialize(null, "\n \t 1 \t \n");
        assertEquals(1, val.asLong());
    }

    @Test
    public void testLong() {
        JsonValue val = Json.deserialize(null, "123456789");
        assertEquals(123456789l, val.asLong());
    }

    @Test
    public void testVeryLong() {
        JsonValue val = Json.deserialize(null, Long.toString( Long.MAX_VALUE ) );
        assertEquals(Long.MAX_VALUE, val.asLong());
    }

    @Test
    public void testTooLong() {
        String stringIn = Long.toString(Long.MAX_VALUE) + "0";
        JsonValue val = Json.deserialize(null, stringIn);
        String stringOut = Json.serialize(val);
        assertEquals(stringIn, stringOut);
    }

    @Test
    public void parseBooleanTrue() {
        JsonValue val = Json.deserialize(null, "true");
        assertEquals(true, val.asBoolean());
    }

    @Test
    public void parseBooleanFalse() {
        JsonValue val = Json.deserialize(null, "false");
        assertEquals(false, val.asBoolean());
    }

    @Test
    public void parseDouble() {
        JsonValue val = Json.deserialize(null, "1.23456");
        assertEquals(1.23456, val.asDouble());
    }

    @Test
    public void parseString() {
        JsonValue val = Json.deserialize(null, "\"str\u00fcng\"");
        assertEquals("strüng", val.asString());
    }

    @Test
    public void parseNull() {
        JsonValue val = Json.deserialize(null, Json.serialize((JsonValue) null));
        assertNull(val);
    }

    @Test
    public void parseArray() {
        JsonValue val = Json.deserialize(null, "[[1],[[1]],[[[1]]]]");
        assertNotNull(val.asArray());
    }

    @Test
    public void parseObject() {
        JsonValue val = Json.deserialize(null, "{\"\":{},\"\":{},\"\":{}}");
        assertNotNull(val.asObject());
        assertNotNull(val.asObject().getObject(""));
    }

}
