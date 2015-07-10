package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

import static com.google.common.base.CharMatcher.WHITESPACE;

public class TrimDeserializer extends StdScalarDeserializer<String> {

    private final StringDeserializer jacksonDeserializer;

    public TrimDeserializer(StringDeserializer jacksonDeserializer) {
        super(String.class);
        this.jacksonDeserializer = jacksonDeserializer;
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext context) throws IOException {

        String output = jacksonDeserializer.deserialize(jp, context);

        if (null != output) {
            output = WHITESPACE.trimFrom(output);
        }

        return output;
    }
}
