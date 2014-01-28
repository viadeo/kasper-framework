// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.viadeo.kasper.KasperResponse.Status;

public final class CommandResponseDeserializer extends JsonDeserializer<CommandResponse> {
    static final Logger LOGGER = LoggerFactory.getLogger(CommandResponseDeserializer.class);

    // ------------------------------------------------------------------------

    public CommandResponseDeserializer() { }

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        Status status = null;
        KasperReason reason = null;
        
        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();
            jp.nextToken();
            
            
            if (ObjectMapperProvider.STATUS.equals(name)) {
                status = jp.readValueAs(Status.class);
            } else if (ObjectMapperProvider.REASONS.equals(name)) {
                if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected START_ARRAY encountered " + jp.getCurrentToken());
                }

                reason = readKasperReason(jp);
            } else if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
                jp.skipChildren();
            }

        }

        return new CommandResponse(status, reason);
    }

    private KasperReason readKasperReason(final JsonParser jp) throws IOException {
        UUID id = null;
        String globalCode = null;
        List<String> messages = new ArrayList<String>();
        
        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_ARRAY)) {
            final String name = jp.getCurrentName();
            if (ObjectMapperProvider.CODE.equals(name)) {
                final String code = jp.nextTextValue();
                if (globalCode == null) {
                    globalCode = code;
                } else if (!globalCode.equals(code)) {
                    LOGGER.warn("Global code[{}] does not match error code[{}]",
                            globalCode, code);
                }
            } else if (ObjectMapperProvider.MESSAGE.equals(name)) {
                messages.add(jp.nextTextValue());
            } else if (ObjectMapperProvider.ID.equals(name)) {
                try {
                    id = UUID.fromString(jp.nextTextValue());
                } catch (final IllegalArgumentException e) {
                    LOGGER.warn("Error when deserializing reason id", e);
                    // Ignore error, id wil be null and managed below
                }
            } // else lets just ignore usermessage and others...
        }
        
        return (null != globalCode) ?
                    (null != id) ?
                            new KasperReason(id, globalCode, messages)
                          : new KasperReason(globalCode, messages)
              : null;
    }

}
