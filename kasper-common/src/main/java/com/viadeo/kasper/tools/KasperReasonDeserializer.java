package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KasperReasonDeserializer extends JsonDeserializer<KasperReason> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperReasonDeserializer.class);

    @Override
    public KasperReason deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return deserialize(jp.readValueAs(ObjectNode.class));
    }

    protected KasperReason deserialize(final ObjectNode root) throws IOException {
        // ID
        final String id;
        if (root.has(ObjectMapperProvider.ID)) {
            id = root.get(ObjectMapperProvider.ID).asText();
        } else {
            id = null;
        }

        // CODE
        final Integer code;
        if (root.has(ObjectMapperProvider.CODE)) {
            code = root.get(ObjectMapperProvider.CODE).asInt(CoreReasonCode.UNKNOWN_REASON.code());
        } else {
            code = 0;
        }

        // LABEL
        final String label;
        if (root.has(ObjectMapperProvider.LABEL)) {
            label = root.get(ObjectMapperProvider.LABEL).asText();
        } else {
            label = "";
        }

        // String CODE
        final String strCode = CoreReasonCode.toString(code, label);

        // MESSAGES
        final List<String> messages = new ArrayList<String>();

        final JsonNode reasonsNode = root.get(ObjectMapperProvider.REASONS);
        if (reasonsNode != null) {
            for (final JsonNode node : reasonsNode) {
                final String message = node.get(ObjectMapperProvider.MESSAGE).asText();
                messages.add(message);
            }
        } else {
            final JsonNode messagesNode = root.get(ObjectMapperProvider.MESSAGES);
            if (messagesNode instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode) messagesNode;
                for (final JsonNode messageNode : Lists.newArrayList(arrayNode.iterator())) {
                    messages.add(messageNode.asText());
                }
            } else {
                messages.add(messagesNode.asText());
            }
        }

        if (null != id) {
            try {
                return new KasperReason(UUID.fromString(id), strCode, messages);
            } catch (final IllegalArgumentException e) {
                LOGGER.warn("Error when deserializing reason id", e);
                return new KasperReason(strCode, messages);
            }
        } else {
            return new KasperReason(strCode, messages);
        }
    }

}
