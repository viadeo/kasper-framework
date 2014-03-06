// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.viadeo.kasper.KasperResponse.Status;

public abstract class KasperResponseDeserializer<R extends KasperResponse> extends JsonDeserializer<R> {
    static final Logger LOGGER = LoggerFactory.getLogger(KasperResponseDeserializer.class);

    // ------------------------------------------------------------------------

    protected KasperResponse deserialize(final ObjectNode root)
            throws IOException {

        // ID
        final String id;
        if (root.has(ObjectMapperProvider.ID)) {
            id = root.get(ObjectMapperProvider.ID).asText();
        } else {
            id = null;
        }

        // STATUS
        Status status = Status.ERROR;
        if (root.has(ObjectMapperProvider.STATUS)) {
            try {
                status = Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
            } catch (final IllegalArgumentException e) {
                LOGGER.error("Unable to determine status", e);
            }
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
        for (final JsonNode node : root.get(ObjectMapperProvider.REASONS)) {
            final String message = node.get(ObjectMapperProvider.MESSAGE).asText();
            messages.add(message);
        }

        // TODO: add Security Token

        if (null != id) {
            try {
                return new KasperResponse(status, new KasperReason(UUID.fromString(id), strCode, messages));
            } catch (final IllegalArgumentException e) {
                LOGGER.warn("Error when deserializing reason id", e);
                return new KasperResponse(Status.ERROR, new KasperReason(strCode, messages));
            }
        } else {
            return new KasperResponse(status, new KasperReason(strCode, messages));
        }

    }

}
