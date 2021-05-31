package net.tx0.jason;

import java.nio.charset.Charset;

/**
 * Configuration class for {@link JsonReader} and {@link JsonWriter}. For an explanation of the individual items see {@link JsonConfigBuilder}.
 *
 * @see JsonConfigBuilder
 * @see JsonFactory
 */
public class JsonConfig {

    private final boolean layout;
    private final boolean strict;
    private final boolean useFloatingPoint;
    private final Charset charset;
    private final int maximumDepth;

    JsonConfig( boolean layout, boolean strict, boolean useFloatingPoint, Charset charset, int maximumDepth ) {
        this.layout = layout;
        this.strict = strict;
        this.useFloatingPoint = useFloatingPoint;
        this.charset = charset;
        this.maximumDepth = maximumDepth;
    }

    public boolean isLayout() {
        return layout;
    }

    public boolean isStrict() {
        return strict;
    }

    public boolean isUseFloatingPoint() {
        return useFloatingPoint;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }
}
