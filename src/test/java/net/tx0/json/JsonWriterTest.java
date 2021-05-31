package net.tx0.json;

import net.tx0.jason.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {

    @Test
    public void testPrettyPrinting() {
        JsonConfig config = new JsonConfigBuilder().setLayout().build();

        String res = Json.serialize(config, Json.deserialize("{ \"blue\": [ 0, 0, 200 ] }"));
        assertEquals(
                "{\n" +
                        "    \"blue\": [\n" +
                        "        0,\n" +
                        "        0,\n" +
                        "        200\n" +
                        "    ]\n" +
                        "}", res);

    }

    @Test
    public void testNoPrettyPrinting() {
        JsonConfig config = new JsonConfigBuilder().setNoLayout().build();

        String res = Json.serialize(config, Json.deserialize("{ \"blue\": [ 0, 0, 200 ] }"));
        assertEquals( "{\"blue\":[0,0,200]}", res );

    }

    @Test
    public void testOneText() {
        JsonConfig config = new JsonConfigBuilder().setLayout().build();

        JsonWriter writer = Json.createWriter(new ByteArrayOutputStream());

        writer.write(1);

        assertThrows( JsonException.class, () -> { writer.write(2); } );
    }

    @Test
    public void testMissingMemberName() {
        JsonConfig config = new JsonConfigBuilder().setLayout().build();

        JsonWriter writer = Json.createWriter(new ByteArrayOutputStream());

        writer.writeStartObject();

        assertThrows( JsonException.class, () -> { writer.write(2); } );

        writer.writeEndObject();
    }



    @Test
    public void testMissingEndObject() {
        JsonConfig config = new JsonConfigBuilder().setLayout().build();

        JsonWriter writer = Json.createWriter(new ByteArrayOutputStream());

        writer.writeStartObject();

        assertThrows( JsonException.class, () -> { writer.close(); } );

    }


}
