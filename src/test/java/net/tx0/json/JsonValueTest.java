package net.tx0.json;

import net.tx0.jason.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonValueTest {

    @Test
    public void testFind() {

        JsonValue sample = Json.deserialize( Sample.SAMPLE_RESOURCE );
        JsonValue colors = Json.deserialize( Sample.COLORS_RESOURCE );

        assertSame( sample, sample.find("") );

        assertEquals( 127, colors.find("chartreuse[0]").asInteger() );
        assertEquals( 127, colors.find(".chartreuse[0]").asInteger() );

        assertThrows( IllegalArgumentException.class, ()-> { sample.find("[1]name"); } );

        assertEquals( "5001", sample.find("[1].topping[0].id").asString() );

    }



}
