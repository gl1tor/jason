package net.tx0.jason;

import java.util.Iterator;

/**
 * A reader for elements of json grammar.
 */
public interface JsonReader extends Iterator<JsonToken> {

	boolean hasNext();
	JsonToken next();

	String getMemberName();
	String getStringValue();
	boolean getBooleanValue();
	Number getNumberValue();

}
