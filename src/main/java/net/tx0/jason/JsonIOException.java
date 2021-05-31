package net.tx0.jason;

import java.io.IOException;

public class JsonIOException extends JsonException {

    public JsonIOException( IOException exception ) {
        super(exception);
    }

    @Override
    public synchronized IOException getCause() {
        return (IOException) super.getCause();
    }
}
