package net.tx0.json;

import net.tx0.jason.JsonResource;

public abstract class Sample {

    /* taken from https://github.com/corysimmons/colors.json/blob/master/colors.json */

    public static final JsonResource COLORS_RESOURCE = JsonResource.forClasspath( Sample.class.getClassLoader(), "colors.json" );

    /* taken from https://opensource.adobe.com/Spry/samples/data_region/JSONDataSetSample.html */

    public static final JsonResource SAMPLE_RESOURCE = JsonResource.forClasspath( Sample.class.getClassLoader(), "sample.json" );

    private Sample() {
    }

}
