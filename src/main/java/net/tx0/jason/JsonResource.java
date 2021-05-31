package net.tx0.jason;

import java.io.*;

public interface JsonResource {

    /**
     * Returns a resource representing the given string.
     * The string needs to be copied around since resources are read as byte streams, use Jason.
     */
    static JsonResource forString( String contents ) {
        return new JsonResource() {
            @Override
            public <T> T readFrom( ReadMethod<T> consumer ) throws IOException {
                try ( ByteArrayInputStream stream = new ByteArrayInputStream(contents.getBytes(Json.getDefaultConfig().getCharset())) ) {
                    return consumer.read(stream);
                }
            }
        };
    }

    /**
     * Returns a resource representing the given file.
     */
    static JsonResource forFile( File file ) {
        return new JsonResource() {
            @Override
            public <T> T readFrom( ReadMethod<T> consumer ) throws IOException {
                try ( FileInputStream stream = new FileInputStream(file) ) {
                    return consumer.read(stream);
                }
            }
        };
    }

    /**
     * Returns a resource representing a given resource on the classpath.
     * @param classLoader the class loader to use, or null to use the context class loader
     */
    static JsonResource forClasspath( ClassLoader classLoader, String name ) {
        final ClassLoader loader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
        return new JsonResource() {
            @Override
            public <T> T readFrom( ReadMethod<T> consumer ) throws IOException {
                InputStream stream = loader.getResourceAsStream(name);
                if ( stream == null )
                    throw new FileNotFoundException("Resource " + name + " not found");
                try {
                    return consumer.read( stream );
                } finally {
                    stream.close();
                }
            }
        };
    }

    interface ReadMethod<T> {
        T read( InputStream stream ) throws IOException;
    }

    <T> T readFrom( ReadMethod<T> consumer ) throws IOException;

}
