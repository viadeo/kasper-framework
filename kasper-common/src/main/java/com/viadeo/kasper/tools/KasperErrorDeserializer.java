package com.viadeo.kasper.tools;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.viadeo.kasper.KasperError;

public class KasperErrorDeserializer extends JsonDeserializer<KasperError> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperErrorDeserializer.class);

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String USERMESSAGE = "userMessage";

    public KasperErrorDeserializer() {
    }

    @Override
    public KasperError deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {

        String code = null;
        String message = null;
        String userMessage = null;
        
        while (!jp.nextToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();
            jp.nextToken();

            if (CODE.equals(name)) {
                code = jp.getValueAsString();
            } else if (MESSAGE.equals(name)) {
                message = jp.getValueAsString();
            }
            if (USERMESSAGE.equals(name)) {
                userMessage = jp.getValueAsString();
            } else {
                LOGGER.warn("Unknown property {} when default mapping KasperError ", name);
                // FIXME do we just ignore unknown properties or take some action?
            }
        }

        return new KasperError(code, message, userMessage);
    }
}
