// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.viadeo.kasper.cqrs.command.ICommandResult.Status;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandResult;

final class KasperCommandResultDeserializer extends StdDeserializer<KasperCommandResult> {
    static final Logger LOGGER = LoggerFactory.getLogger(KasperCommandResultDeserializer.class);
    
    private static final String STATUS = "status";
    
    // ------------------------------------------------------------------------
    
    KasperCommandResultDeserializer() {
        super(KasperCommandResult.class);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public KasperCommandResult deserialize(final JsonParser jp, final DeserializationContext ctxt) 
            throws IOException {
        
        Status status = null;

        while (!jp.nextToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();
            jp.nextToken();
            
            if (STATUS.equals(name)) {
                status = jp.readValueAs(Status.class);
            } else {
                LOGGER.warn("Unknown property when default mapping DTO");
                // FIXME do we just ignore unknown properties or take some action?
            }
        }

        return new KasperCommandResult(status);
    }
    
}