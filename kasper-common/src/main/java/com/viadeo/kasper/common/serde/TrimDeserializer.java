// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

import static com.google.common.base.CharMatcher.WHITESPACE;
import static com.google.common.base.Preconditions.checkNotNull;

public class TrimDeserializer extends StdScalarDeserializer<String> {

    private final StringDeserializer jacksonDeserializer;

    // ------------------------------------------------------------------------

    public TrimDeserializer(final StringDeserializer jacksonDeserializer) {
        super(String.class);
        this.jacksonDeserializer = checkNotNull(jacksonDeserializer);
    }

    // ------------------------------------------------------------------------

    @Override
    public String deserialize(final JsonParser jp, final DeserializationContext context) throws IOException {

        String output = jacksonDeserializer.deserialize(jp, context);
        if (null != output) {
            output = WHITESPACE.trimFrom(output);
        }

        return output;
    }

}
