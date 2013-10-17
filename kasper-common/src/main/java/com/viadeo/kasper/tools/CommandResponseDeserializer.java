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
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.CommandResponse.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CommandResponseDeserializer extends JsonDeserializer<CommandResponse> {
    static final Logger LOGGER = LoggerFactory.getLogger(CommandResponseDeserializer.class);

    // ------------------------------------------------------------------------

    public CommandResponseDeserializer() { }

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        Status status = null;
        KasperError error = null;
        
        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();
            jp.nextToken();
            
            
            if (ObjectMapperProvider.STATUS.equals(name)) {
                status = jp.readValueAs(Status.class);
            } else if (ObjectMapperProvider.ERRORS.equals(name)) {
                if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected START_ARRAY encountered " + jp.getCurrentToken());
                }

                error = readKasperError(jp);
            }
        }

        return new CommandResponse(status, error);
    }

    private KasperError readKasperError(final JsonParser jp) throws IOException {
        String globalCode = null;
        List<String> messages = new ArrayList<String>();
        
        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_ARRAY)) {
            String name = jp.getCurrentName();
            if (ObjectMapperProvider.CODE.equals(name)) {
                String code = jp.nextTextValue();
                if (globalCode == null) {
                    globalCode = code;
                } else if (!globalCode.equals(code)) {
                    LOGGER.warn("Global code[{}] does not match error code[{}]",
                            globalCode, code);
                }
            } else if (ObjectMapperProvider.MESSAGE.equals(name)) {
                messages.add(jp.nextTextValue());
            } // else lets just ignore usermessage and others...
        }
        
        return globalCode != null ? new KasperError(globalCode, messages) : null;
    }

}
