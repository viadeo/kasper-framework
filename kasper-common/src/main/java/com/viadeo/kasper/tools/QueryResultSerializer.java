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
import com.viadeo.kasper.cqrs.query.QueryResult;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class QueryResultSerializer extends JsonSerializer<QueryResult> {
    
    @Override
    public void serialize(QueryResult value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        if (value.isError()) {
            jgen.writeStartObject();

            // lets write a boolean telling that this is an error, can be useful
            // for js consumers
            jgen.writeFieldName(ObjectMapperProvider.ERROR);
            jgen.writeBoolean(true);
            
            KasperError error = value.getError();

            jgen.writeFieldName(ObjectMapperProvider.MESSAGE);
            jgen.writeString(error.getCode());

            jgen.writeFieldName(ObjectMapperProvider.ERRORS);

            jgen.writeStartArray();
            for (String message : error.getMessages()) {
                jgen.writeStartObject();
                
                jgen.writeStringField(ObjectMapperProvider.CODE, error.getCode());
                jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);
                jgen.writeNullField(ObjectMapperProvider.USERMESSAGE);
                
                jgen.writeEndObject();
            }
            jgen.writeEndArray();

            jgen.writeEndObject();
        } else {
            jgen.writeObject(value.getAnswer());
        }
    }
}
