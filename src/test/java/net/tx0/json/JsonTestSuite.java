package net.tx0.json;

import net.tx0.jason.Json;
import net.tx0.jason.JsonException;
import net.tx0.jason.JsonResource;
import net.tx0.jason.JsonValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Dynamic test suite build around files from https://github.com/nst/JSONTestSuite.
 */
public class JsonTestSuite {

    @TestFactory
    @Disabled
    public Collection<DynamicTest> runTestSuite() throws IOException {

        List<DynamicTest> tests = new ArrayList<>();

        for ( File file : new File("JSONTestSuite/test_parsing").listFiles() ) {

            String name = file.getName();

            if ( !name.endsWith(".json") )
                continue;

            final char t = name.charAt(0);

            tests.add( DynamicTest.dynamicTest(name, ()->{

                boolean completed;
                try {
                    JsonValue text = Json.deserialize( null, JsonResource.forFile( file ) );
                    completed = true;
                } catch ( JsonException e ) {
                    e.printStackTrace();
                    completed = false;
                }

                if ( t == 'y' ) {
                    assertTrue(completed);
                } else if ( t == 'n' ) {
                    assertFalse(completed);
                }

            }));

        }

        return tests;
    }

}
