package net.tx0.jason;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Builder for {@link JsonConfig}s. A freshly constructed Builder builds the default configuration.
 *
 * @see JsonConfig
 */
public class JsonConfigBuilder {

    private boolean layout = true;
    private boolean strict = true;
    private boolean useFloatingPoint = false;
    private Charset charset = StandardCharsets.UTF_8;
    private int maximumDepth = 1000;

    public JsonConfigBuilder() {
    }

    /**
     * The {@link JsonWriter} instances produce layout
     * to pretty print the output.
     */
    public JsonConfigBuilder setLayout() {
        this.layout = true;
        return this;
    }

    /**
     * The {@link JsonWriter} instances produce compact json without layout characters.
     */
    public JsonConfigBuilder setNoLayout() {
        this.layout = false;
        return this;
    }

    /**
     * The {@link JsonReader} instances try to parse fractional numbers
     * as doubles.
     */
    public void setUseFloatingPoint() {
        this.useFloatingPoint = true;
    }

    /**
     * The {@link JsonReader} instances use integer primitives or {@link java.math.BigDecimal}.
     * as doubles.
     */
    public void setNoUseFloatingPoint() {
        this.useFloatingPoint = true;
    }

    /**
     * Only accept json texts valid according to RFC 7159.
     */
    public JsonConfigBuilder setStrict() {
        this.strict = true;
        return this;
    }

    /**
     * Allows the {@link JsonReader} instances to accept invalid JSON according to RFC 7159.
     * What superset exactly will be accepted is not specified yet.
     */
    public JsonConfigBuilder setNonStrict() {
        this.strict = false;
        return this;
    }

    /**
     * Sets the default charset to use if none is specified.
     */
    public JsonConfigBuilder setCharset( Charset charset ) {
        this.charset = charset;
        return this;
    }

    /**
     * The maximum depth supported by {@link JsonReader} instances.
     */
    public JsonConfigBuilder setMaximumDepth( int maximumDepth ) {
        this.maximumDepth = maximumDepth;
        return this;
    }

    public JsonConfig build() {
        return new JsonConfig(layout, strict, useFloatingPoint, charset, maximumDepth);
    }

}
