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

        // lets write a boolean telling that this is an reason, can be useful
        // for js consumers
        jgen.writeFieldName(ObjectMapperProvider.REASON);
        jgen.writeBoolean( ! value.isOK());

         /* FIXME - retro-compatibility - TO BE REMOVED */
        jgen.writeFieldName(ObjectMapperProvider.ERRORS);
        this.writeReasons(value, jgen);

        jgen.writeFieldName(ObjectMapperProvider.REASONS);
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

                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();
    }

}
