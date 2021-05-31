package net.tx0.jason;

public class JsonBoolean extends JsonScalar {

	static final JsonBoolean TRUE = new JsonBoolean( true );
	static final JsonBoolean FALSE = new JsonBoolean( false );
	
	private final boolean value;

	private JsonBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public JsonValueType getType() {
		return JsonValueType.BOOLEAN;
	}

	@Override
	void write(JsonWriter writer) {
		writer.write(value);
	}

	@Override
	public boolean asBoolean() {
		return value;
	}

}
