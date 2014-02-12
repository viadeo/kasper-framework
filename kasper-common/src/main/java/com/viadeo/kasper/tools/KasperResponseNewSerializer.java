// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.cqrs.command.CommandResponse;

import java.io.IOException;

public abstract class KasperResponseNewSerializer<R extends KasperResponse> extends JsonSerializer<R> {

    @Override
    public void serialize(final R value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {
        start(jgen);
        body(value, jgen, provider);
        end(jgen);
    }

    // ------------------------------------------------------------------------

    protected void start(final JsonGenerator jgen) throws IOException {
        jgen.writeStartObject();;
    }

    protected void body(final R value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {

        jgen.writeStringField(ObjectMapperProvider.STATUS, value.getStatus().name());

        final KasperReason reason = value.getReason();
        if (null != reason) {
            jgen.writeStringField(ObjectMapperProvider.ID, reason.getId().toString());
            jgen.writeNumberField(ObjectMapperProvider.CODE, reason.getReasonCode());
            jgen.writeStringField(ObjectMapperProvider.LABEL, reason.getLabel());
        }

        jgen.writeFieldName(ObjectMapperProvider.REASON);
        jgen.writeBoolean( ! value.isOK());

        jgen.writeFieldName(ObjectMapperProvider.REASONS);
        jgen.writeStartArray();
        if (! value.isOK()) {
            for (String message : reason.getMessages()) {
                jgen.writeStartObject();
                jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);
                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();
    }

    protected void end(final JsonGenerator jgen) throws IOException {
        jgen.writeEndObject();
    }

}
