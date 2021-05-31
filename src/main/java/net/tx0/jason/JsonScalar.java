package net.tx0.jason;

public abstract class JsonScalar extends JsonValue {
    @Override
    public JsonScalar asScalar() {
        return this;
    }
}
