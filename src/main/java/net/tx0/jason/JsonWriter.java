package net.tx0.jason;

/**
 * Write individual JSON tokens.
 */
public interface JsonWriter {

	/**
	 * Finishes writing and flushes any buffers, if necessary.
	 * Checks whether the generated json text is well formed.
	 * Does <em>not</em> close any underlying output stream or writer.
 	 */
	void close();

	void writeNull();

	void write( String string );
	void write( Number number );
	void write( boolean bool );

	void writeBeginArray();
	void writeEndArray();

	void writeStartObject();
	void writeEndObject();

	void writeMember( String name );

	/**
	 * Writes any sequence of permitted whitespace to the output.
	 *
	 * This method can be used to format the JSON output in a custom way.
	 */
	void writeLayout( String layout );

}
