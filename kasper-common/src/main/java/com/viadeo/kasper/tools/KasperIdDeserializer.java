// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;

import java.io.IOException;

public final class KasperIdDeserializer extends JsonDeserializer<KasperID> {

    @Override
    public KasperID deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final String textValue = jp.getText();
        return new DefaultKasperId(textValue);
    }

}
