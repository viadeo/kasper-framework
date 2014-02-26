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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.viadeo.kasper.KasperResponse.Status;

public final class CommandResponseDeserializer extends KasperResponseDeserializer<CommandResponse> {
    static final Logger LOGGER = LoggerFactory.getLogger(CommandResponseDeserializer.class);

    // ------------------------------------------------------------------------

    public CommandResponseDeserializer() { }

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

        final ObjectNode root = jp.readValueAs(ObjectNode.class);

        if (root.has(ObjectMapperProvider.ID)) {
            return deserialize_new(root);
        } else {
            return deserialize_old(root);
        }
    }

    // ------------------------------------------------------------------------

    public CommandResponse deserialize_old(final ObjectNode root) throws IOException {
        Status status = Status.ERROR;

        if (root.has(ObjectMapperProvider.STATUS)) {
            try {
                status = Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
            } catch (final IllegalArgumentException e) {
                LOGGER.error("Unable to determine status", e);
            }
        }

        String id = null;
        String globalCode = "";
        final List<String> messages = new ArrayList<String>();
        for (final JsonNode node : root.get(ObjectMapperProvider.REASONS)) {
            id = node.get(ObjectMapperProvider.ID).asText();
            final String code = node.get(ObjectMapperProvider.CODE).asText();
            final String message = node.get(ObjectMapperProvider.MESSAGE).asText();
            messages.add(message);
            globalCode = code;
        }

        if (null != id) {
            try {
                return new CommandResponse(status, new KasperReason(UUID.fromString(id), globalCode, messages));
            } catch (final IllegalArgumentException e) {
                LOGGER.warn("Error when deserializing reason id", e);
                return CommandResponse.error(new KasperReason(globalCode, messages));
            }
        } else {
            KasperReason reason = null;
            if (! status.equals(Status.OK)) {
                reason = new KasperReason(globalCode, messages);
            }
            return new CommandResponse(status, reason);
        }

    }

    // ------------------------------------------------------------------------

    public CommandResponse deserialize_new(final ObjectNode root) throws IOException {
        final KasperResponse kasperResponse = super.deserialize(root);
        return new CommandResponse(kasperResponse);
    }

}
