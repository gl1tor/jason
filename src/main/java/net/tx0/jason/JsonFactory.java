package net.tx0.jason;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Factory to create {@link JsonReader} and {@link JsonWriter} instances.
 *
 * The factory wraps a configuration and produces instances configured as mandated by the supplied configuration.
 *
 * Every factory method has a static counterpart in {@link Json} which utilize the default configuration.
 *
 * The factory instance is thread-safe, the created reader and writer instances are not thread-safe.
 */
public final class JsonFactory {

    private final JsonConfig config;

    public JsonFactory( JsonConfig config ) {
        this.config = config;
    }

    /**
     * Creates a {@link JsonReader} to read from the supplied {@link Reader}.
     *
     * The caller is still responsible to close the supplied reader.
     */
    public JsonReader createReader( Reader reader ) {
        return Json.createReader( config, reader );
    }

    /**
     * Creates a {@link JsonReader} to read from the supplied {@link InputStream}.
     *
     * The caller is still responsible to close the input stream.
     */
    public JsonReader createReader( InputStream inputStream ) {
        return Json.createReader( config, inputStream );
    }

    /**
     * Creates a {@link JsonWriter} to write to the supplied {@link OutputStream}.
     *
     * The caller is still responsible to close the output stream.
     */
    public JsonWriter createWriter( OutputStream outputStream ) {
        return Json.createWriter( config, outputStream );
    }

    /**
     * Creates a {@link JsonWriter} to write to the supplied {@link Writer}.
     *
     * The caller is still responsible to close the supplied writer.
     */
    public JsonWriter createWriter( Writer writer ) {
        return Json.createWriter( config, writer );
    }

}
