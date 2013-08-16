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
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;

import java.io.IOException;

class KasperQueryExceptionSerializer extends JsonSerializer<KasperQueryException> {

    @Override
    public void serialize(final KasperQueryException value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();

        // lets write a boolean telling that this is an error, can be useful for js consumers
        jgen.writeFieldName(ObjectMapperProvider.ERROR);
        jgen.writeBoolean(true);

        jgen.writeFieldName(ObjectMapperProvider.MESSAGE);
        jgen.writeString(value.getMessage());

        jgen.writeFieldName(ObjectMapperProvider.ERRORS);

        jgen.writeStartArray();
        if (value.getErrors().isPresent()) {
            for (final KasperError error : value.getErrors().get()) {
                jgen.writeObject(error);
            }
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

}
