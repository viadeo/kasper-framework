// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

class KasperQueryExceptionDeserializer extends JsonDeserializer<KasperQueryException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperQueryExceptionDeserializer.class);

    private final TypeReference<List<KasperError>> listOfKasperErrorType = new TypeReference<List<KasperError>>() {};

    @Override
    public KasperQueryException deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        String message = null;
        List<KasperError> errors = null;

        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();

            if (name.contentEquals(ObjectMapperProvider.MESSAGE)) {
                message = jp.getValueAsString();
            } else if (name.contentEquals(ObjectMapperProvider.ERRORS)) {
                jp.nextToken();
                errors = jp.readValueAs(listOfKasperErrorType);
            } else {
                LOGGER.warn("Unknown property[{}]", name);
            }

        }

        return new KasperQueryException(message, null, errors);
    }

}
