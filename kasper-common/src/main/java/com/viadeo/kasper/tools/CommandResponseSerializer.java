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
        jgen.writeFieldName(ObjectMapperProvider.ERRORS);
        jgen.writeStartArray();
        if (value.isError()) {
            final KasperReason error = value.getReason();
            for (String message : error.getMessages()) {
                jgen.writeStartObject();
                
                jgen.writeStringField(ObjectMapperProvider.CODE, error.getCode());
                jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);
                jgen.writeNullField(ObjectMapperProvider.USERMESSAGE);
                
                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }

}
