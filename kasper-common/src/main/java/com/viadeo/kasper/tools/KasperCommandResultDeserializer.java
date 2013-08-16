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
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.command.CommandResult.ResultBuilder;
import com.viadeo.kasper.cqrs.command.CommandResult.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class KasperCommandResultDeserializer extends JsonDeserializer<CommandResult> {
    
    static final Logger LOGGER = LoggerFactory.getLogger(KasperCommandResultDeserializer.class);

    private static final String STATUS = "status";
    private static final String ERRORS = "errors";
    private static final String ERROR = "error";

    // ------------------------------------------------------------------------

    public KasperCommandResultDeserializer() { }

    // ------------------------------------------------------------------------

    @Override
    public CommandResult deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final ResultBuilder result = new ResultBuilder();

        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();
            jp.nextToken();

            if (STATUS.equals(name)) {
                result.status(jp.readValueAs(Status.class));
            } else if (ERRORS.equals(name)) {
                if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected START_ARRAY encountered " + jp.getCurrentToken());
                }

                while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_ARRAY)) {
                    if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
                        result.addError(jp.readValueAs(KasperError.class));
                    }
                }
            }
            if (!ERROR.equals(name)) {
                LOGGER.warn("Unknown property when default mapping to a Result");
                // FIXME do we just ignore unknown properties or take some action?
            }
        }

        return result.create();
    }

}
