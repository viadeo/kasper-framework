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
import com.viadeo.kasper.cqrs.command.CommandResponse;

import java.io.IOException;

public class CommandResponseSerializer extends JsonSerializer<CommandResponse> {

    @Override
    public void serialize(final CommandResponse value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField(ObjectMapperProvider.STATUS, value.getStatus().name());

        jgen.writeFieldName(ObjectMapperProvider.REASONS);
        this.writeReasons(value, jgen);

        /* FIXME - retro-compatibility - TO BE REMOVED */
        jgen.writeFieldName(ObjectMapperProvider.ERRORS);
        this.writeReasons(value, jgen);

        jgen.writeEndObject();
    }

    private void writeReasons(final CommandResponse value, final JsonGenerator jgen) throws IOException {
        jgen.writeStartArray();
        if (!value.isOK()) {
            final KasperReason reason = value.getReason();
            for (String message : reason.getMessages()) {
                jgen.writeStartObject();

                jgen.writeStringField(ObjectMapperProvider.ID, reason.getId().toString());
                jgen.writeStringField(ObjectMapperProvider.CODE, reason.getCode());
                jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);
                jgen.writeNullField(ObjectMapperProvider.USERMESSAGE);

                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();
    }

}
