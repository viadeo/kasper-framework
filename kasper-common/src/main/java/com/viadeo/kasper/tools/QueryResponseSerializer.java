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
        if (!value.isOK()) {
            jgen.writeStartObject();

            // lets write a boolean telling that this is an reason, can be useful
            // for js consumers
            jgen.writeFieldName(ObjectMapperProvider.ERROR);
            jgen.writeBoolean(true);
            
            KasperReason reason = value.getReason();

            jgen.writeFieldName(ObjectMapperProvider.MESSAGE);
            jgen.writeString(reason.getCode());

            jgen.writeFieldName(ObjectMapperProvider.REASONS);
            this.writeReason(reason, jgen);

            /* FIXME - retro-compatibility - TO BE REMOVED */
            jgen.writeFieldName(ObjectMapperProvider.ERRORS);
            this.writeReason(reason, jgen);

            jgen.writeEndObject();
        } else {
            jgen.writeObject(value.getResult());
        }
    }

    private void writeReason(final KasperReason reason, final JsonGenerator jgen) throws IOException {
        jgen.writeStartArray();
        for (String message : reason.getMessages()) {
            jgen.writeStartObject();

            jgen.writeStringField(ObjectMapperProvider.ID, reason.getId().toString());
            jgen.writeStringField(ObjectMapperProvider.CODE, reason.getCode());
            jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);
            jgen.writeNullField(ObjectMapperProvider.USERMESSAGE);

            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }

}
