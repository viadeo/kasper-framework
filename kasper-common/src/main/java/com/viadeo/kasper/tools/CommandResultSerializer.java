package com.viadeo.kasper.tools;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;

public class CommandResultSerializer extends JsonSerializer<CommandResult> {

    @Override
    public void serialize(CommandResult value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField(ObjectMapperProvider.STATUS, value.getStatus().name());
        jgen.writeFieldName(ObjectMapperProvider.ERRORS);
        jgen.writeStartArray();
        if (value.isError()) {
            KasperError error = value.getError();
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
