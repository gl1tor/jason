package net.tx0.jason;

/**
 * Parts of the json grammar as used by {@link JsonReader}.
 */
public enum JsonToken {

	BEGIN_OBJECT,
	END_OBJECT, 
	BEGIN_ARRAY,
	END_ARRAY,
	MEMBER_NAME,
	NULL,
	BOOLEAN,
	NUMBER,
	STRING,

}
