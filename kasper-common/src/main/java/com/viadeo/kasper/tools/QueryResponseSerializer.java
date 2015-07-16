// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.viadeo.kasper.api.component.query.QueryResponse;

import java.io.IOException;

public class QueryResponseSerializer extends KasperResponseSerializer<QueryResponse> {

    @Override
    public void serialize(final QueryResponse value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {

        if ( ! value.isOK()) {
            super.serialize(value, jgen, provider);
        } else {
            jgen.writeObject(value.getResult());
        }

    }

}
