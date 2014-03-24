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
import com.viadeo.kasper.cqrs.query.QueryResponse;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class QueryResponseSerializer extends JsonSerializer<QueryResponse> {
    
    @Override
    public void serialize(QueryResponse value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        if ( ! value.isOK()) {

            jgen.writeStartObject();
            jgen.writeStringField(ObjectMapperProvider.STATUS, value.getStatus().name());

            // lets write a boolean telling that this is an reason, can be useful
            // for js consumers
            jgen.writeFieldName(ObjectMapperProvider.REASON);
            jgen.writeBoolean( ! value.isOK());

            KasperReason reason = value.getReason();

            jgen.writeFieldName(ObjectMapperProvider.MESSAGE);
            jgen.writeString(reason.getCode());

            jgen.writeFieldName(ObjectMapperProvider.REASONS);
            jgen.writeStartArray();
            for (final String message : reason.getMessages()) {
                jgen.writeStartObject();

                jgen.writeStringField(ObjectMapperProvider.ID, reason.getId().toString());
                jgen.writeStringField(ObjectMapperProvider.CODE, reason.getCode());
                jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);

                jgen.writeEndObject();
            }
            jgen.writeEndArray();

            jgen.writeEndObject();

        } else {
            jgen.writeObject(value.getResult());
        }
    }

}
