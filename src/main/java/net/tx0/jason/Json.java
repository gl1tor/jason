package net.tx0.jason;

import java.io.*;
import java.nio.charset.Charset;

/**
 * <h1>Json serialization library</h1>
 *
 * <h2>Features</h2>
 *
 * <ul>
 *     <li>Provides JSON serialization and deserialization</li>
 *     <li>Json arrays and objects implement {@link java.util.List}s and {@link java.util.Map}s </li>
 *     <li>Standards (RFC 7159) compliant</li>
 *     <li>Opinionated convenience methods and defaults</li>
 *     <li>Takes and supplies Java primitives, uses Java null</li>
 *     <li>No dependencies</li>
 *     <li>No checked exceptions</li>
 *     <li>No marshalling of user types</li>
 * </ul>
 *
 * <h2>Usage and overview</h2>
 *
 * <p>
 * Reading or writing JSON is ultimately done using either a {@link JsonReader} or a {@link JsonWriter}, respectively.
 * This class provides overloaded static methods to construct instances of these interfaces.
 * In addition to using these classes directly there are a number of convenience methods provided that either accept
 * a reader or writer or create one on their own. Readers and writers require a {@link JsonConfig}, methods that read
 * or write json but don't take neither a reader nor a config as an argument rely on a default configuration.
 * </p>
 *
 * <p>
 *     The {@link JsonFactory} is another way to construct instances of {@link JsonReader} and {@link JsonWriter}.
 * </p>
 *
 * <table>
 *     <tr>
 *         <th>Interface</th>
 *         <th>Explicit config</th>
 *         <th>Default config</th>
 *         <th>Using factory's config</th>
 *     </tr>
 *     <tr>
 *         <td>{@link JsonReader}</td>
 *         <td>{@link #createReader(JsonConfig, InputStream)}</td>
 *         <td>{@link #createReader(InputStream)}</td>
 *         <td>{@link JsonFactory#createReader(InputStream)}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link JsonWriter}</td>
 *         <td>{@link #createWriter(JsonConfig, OutputStream)}</td>
 *         <td>{@link #createWriter(OutputStream)}</td>
 *         <td>{@link JsonFactory#createWriter(OutputStream)}</td>
 *     </tr>
 * </table>
 *
 * <h2>Serialization and deserialization</h2>
 *
 * <p>
 * This class provides several overloaded methods to serialize an object tree ({@link JsonValue} into a JSON text, and
 * vice versa methods to deserialize a JSON text into a generic object tree.
 * </p>
 *
 * <p>
 * Various variants exist and differ by configuration used and the target written to, or read from in case of deserialization.
 * {@link JsonResource} provides a way to deserialize from general resources.
 * </p>
 *
 * <p>
 *     The object tree is modelled by a type hierarchy starting at {@link JsonValue}. {@link JsonObject} and {@link JsonArray}
 *     being two prominent subclasses providing various methods to easily access contained values.
 * </p>
 *
 * <h2>Example</h2>
 *
 *  <pre>
 *      JsonValue value = Json.deserialize("{ \"naem\" : \"Tom\" }");
 *      value.asObject().rename( "naem", "name" );
 *      System.out.print( Json.serialize( value ) );
 *  </pre>
 *
 * <h2>Exceptions</h2>
 *
 * <p>
 * This library does not throw checked exceptions. It wraps {@link IOException}s which occur during reading
 * or writing in {@link JsonIOException}s. This is a trade-off between usability in callbacks and pure in-memory
 * operations (i.e. parsing a string), and consistency with remaining I/O related code.
 * </p>
 *
 * <p>An exception to this are methods that take {@link File}s as an argument and perform I/O on these files.</p>
 *
 * <h2>Closable streams</h2>
 *
 * This api takes {@link InputStream}s and {@link OutputStream}s at various
 * points and generally doesn't close them. The streams can be reused, f.i.
 * serialize can be called multiple times with the same output stream, or
 * the stream could be interleaved with other data.
 *
 * @see JsonFactory
 * @see JsonValue
 */
public abstract class Json {

    private static final class DefaultConfigHolder {
        private static final JsonConfig INSTANCE = new JsonConfigBuilder().build();
    }

    static JsonConfig getDefaultConfig() {
        return DefaultConfigHolder.INSTANCE;
    }

    private Json() {
    }


    public static JsonValue deserialize( JsonResource resource ) {
        return deserialize( null, resource );
    }

    public static JsonValue deserialize( String string ) {
        return deserialize( null, string );
    }

    public static JsonValue deserialize( File file ) throws IOException {
        return deserialize( null, file );
    }

    public static String serialize( JsonValue value ) {
        return serialize(null, value);
    }

    public static void serialize( JsonValue value, File file ) throws IOException {
        serialize( null, value, file, null );
    }

    public static void serialize( JsonValue value, File file, Charset encoding ) throws IOException {
        serialize( null, value, file, encoding );
    }

    public static void serialize( JsonWriter writer ) {
        serialize( null, writer );
    }

    public static void serialize( JsonValue value, OutputStream outputStream ) {
        serialize( value, null, outputStream );
    }

    public static void serialize( JsonValue value, Writer writer ) {
        serialize( value, null, writer );
    }

    public static JsonReader createReader( Reader reader ) {
        return createReader( null, reader );
    }

    public static JsonReader createReader( InputStream inputStream ) {
        return createReader( null, inputStream );
    }

    public static JsonWriter createWriter( OutputStream outputStream ) {
        return createWriter( null, outputStream );
    }

    public static JsonWriter createWriter( Writer writer ) {
        return createWriter( null, writer );
    }

    public static JsonValue deserialize( JsonConfig config, JsonResource resource ) {
        try {
            return resource.readFrom( (is)-> deserialize( createReader( config, is ) ) );
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    public static JsonValue deserialize( JsonConfig config, String string ) {
        return deserialize( config, JsonResource.forString( string ) );
    }

    public static JsonValue deserialize( JsonConfig config, File file ) throws IOException {
        try {
            return deserialize(config, JsonResource.forFile(file));
        } catch ( JsonIOException e ) {
            throw e.getCause();
        }
    }

    public static JsonValue deserialize( JsonConfig config, ClassLoader classLoader, String name ) {
        return deserialize( config, JsonResource.forClasspath( classLoader, name ) );
    }

    public static JsonValue deserialize( JsonReader reader ) {
        return JsonParser.parseText(reader);
    }

    public static String serialize( JsonConfig config, JsonValue value ) {
        StringWriter sw = new StringWriter();
        serialize( value, config, sw );
        return sw.toString();
    }

    public static void serialize( JsonConfig config, JsonValue value, File file ) throws IOException {
        serialize( config, value, file, null );
    }

    public static void serialize( JsonConfig config, JsonValue value, File file, Charset encoding ) throws IOException {
        if ( config == null )
            config = getDefaultConfig();
        if ( encoding == null )
            encoding = config.getCharset();
        boolean notClosed = true;
        FileOutputStream outputStream = new FileOutputStream(file);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, encoding);
            try {
                serialize(value, config, writer);
            } catch ( JsonIOException e ) {
                throw e.getCause();
            } finally {
                writer.close();
                notClosed = false;
            }
        } finally {
            if ( notClosed )
                outputStream.close();
        }
    }

    public static void serialize( JsonValue value, JsonWriter writer ) {
        if ( value == null ) {
            writer.writeNull();
        } else {
            value.write(writer);
        }
    }

    public static void serialize( JsonValue value, JsonConfig config, OutputStream outputStream ) {
        JsonWriter writer = createWriter(config, outputStream);
        serialize(value,writer);
        writer.close(); // no finally
    }

    public static void serialize( JsonValue value, JsonConfig config, Writer writer ) {
        JsonWriter jsonWriter = createWriter(config, writer);
        serialize(value,jsonWriter);
        jsonWriter.close(); // no finally
    }

    public static JsonReader createReader( JsonConfig config, Reader reader ) {
        if ( config == null )
            config = getDefaultConfig();
        return new JsonReaderImpl( config, new JsonScanner( config, reader ) );
    }

    public static JsonReader createReader( JsonConfig config, InputStream inputStream ) {
        if ( config == null )
            config = getDefaultConfig();
        return new JsonReaderImpl( config, new JsonScanner( config, inputStream) );
    }

    public static JsonWriter createWriter( JsonConfig config, OutputStream outputStream ) {
        if ( config == null )
            config = getDefaultConfig();
        return createWriter( config, new OutputStreamWriter( outputStream, config.getCharset() ) );
    }

    public static JsonWriter createWriter( JsonConfig config, Writer writer ) {
        if ( config == null )
            config = getDefaultConfig();
        return new JsonWriterImpl( config, writer );
    }

    /**
     * Copies json tokens from the supplied source to the supplied target.
     */
    public static void copy( JsonReader source, JsonWriter target ) {

        while ( source.hasNext() ) {

            JsonToken tag = source.next();

            switch ( tag ) {
                case BOOLEAN:
                    target.write( source.getBooleanValue() );
                    break;
                case END_ARRAY:
                    target.writeEndArray();
                    break;
                case END_OBJECT:
                    target.writeEndObject();
                    break;
                case NUMBER:
                    target.write( source.getNumberValue() );
                    break;
                case NULL:
                    target.writeNull();
                    break;
                case BEGIN_ARRAY:
                    target.writeBeginArray();
                    break;
                case MEMBER_NAME:
                    target.writeMember( source.getMemberName() );
                    break;
                case BEGIN_OBJECT:
                    target.writeStartObject();
                    break;
                case STRING:
                    target.write( source.getStringValue() );
                    break;
                default:
                    throw new IllegalStateException( "Unknown json token encountered" );
            }
        }
    }

}
