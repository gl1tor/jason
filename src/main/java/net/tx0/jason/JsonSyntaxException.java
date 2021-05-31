package net.tx0.jason;

public class JsonSyntaxException extends JsonException {

    private final JsonLocation location;

    JsonSyntaxException( String message, JsonLocation location ) {
        super(message + " at " + location );
        this.location = location;
    }

    public JsonLocation getLocation() {
        return location;
    }

}
