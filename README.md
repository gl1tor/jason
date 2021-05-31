# Jason

Json serialization library

## Features

 - Provides JSON serialization and deserialization
 - Json arrays and objects implement java.util.List and java.util.Map 
 - Standards (RFC 7159) compliant
 - Opinionated convenience methods and defaults
 - Takes and supplies Java primitives, uses Java null
 - No dependencies
 - No checked exceptions
 - No marshalling of user types

## Usage and overview<

Reading or writing JSON is ultimately done using either a JsonReader or a JsonWriter, respectively.
This class provides overloaded static methods to construct instances of these interfaces.
In addition to using these classes directly there are a number of convenience methods provided that either accept
a reader or writer or create one on their own. Readers and writers require a JsonConfig, methods that read
or write json but take neither a reader, nor a config as an argument rely on a default configuration.

The JsonFactory is another way to construct instances of JsonReader and JsonWriter.

Interface | Explicit config | Default config | Factory's config
-----------|----------------|----------------|----------------------
JsonReader | Json.createReader( JsonConfig, InputStream ) | Json.createReader( InputStream ) | factory.createReader(InputStream)
JsonWriter | Json.createWriter( JsonConfig, OutputStream ) | Json.createWriter( OutputStream ) | factory.createWriter(OutputStream)

## Serialization and deserialization

This class provides several overloaded methods to serialize an object tree (JsonValue into a JSON text, and
vice versa methods to deserialize a JSON text into a generic object tree.

Various variants exist and differ by configuration used and the target written to, or read from in case of deserialization.
JsonResource provides a way to deserialize from general resources.

The object tree is modelled by a type hierarchy starting at JsonValue. JsonObject and JsonArray
being two prominent subclasses providing various methods to easily access contained values.

## Example

```java
JsonValue value = Json.deserialize("{ \"naem\" : \"Tom\" }");
value.asObject().rename( "naem", "name" );
System.out.print( Json.serialize( value ) );
```

## Exceptions
This library does not throw checked exceptions. It wraps IOExceptions which occur during reading
or writing in JsonIOExceptions. This is a trade-off between usability in callbacks and pure in-memory
operations (i.e. parsing a string), and consistency with remaining I/O related code.
 
An exception to this are methods that take Files as an argument and perform I/O on these files.

## Closable streams
This API takes InputStreams and OutputStreams at various
points and generally doesn't close them. The streams can be reused, f.i.
serialize can be called multiple times with the same output stream, or
the stream could be interleaved with other data.
