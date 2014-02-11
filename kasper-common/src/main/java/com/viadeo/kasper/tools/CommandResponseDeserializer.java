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
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.QueryResponse;
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

        // TODO: add Security Token

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

       /*
        while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            final String name = jp.getCurrentName();
            jp.nextToken();

            if (ObjectMapperProvider.STATUS.equals(name)) {
                status = jp.readValueAs(Status.class);
            } else if (ObjectMapperProvider.REASONS.equals(name)) {
                if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected START_ARRAY encountered " + jp.getCurrentToken());
                }

                UUID id = null;
                String globalCode = null;
                List<String> messages = new ArrayList<String>();

                while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_ARRAY)) {
                    final String tname = jp.getCurrentName();
                    if (ObjectMapperProvider.CODE.equals(tname)) {
                        final String code = jp.nextTextValue();
                        if (globalCode == null) {
                            globalCode = code;
                        } else if (!globalCode.equals(code)) {
                            LOGGER.warn("Global code[{}] does not match error code[{}]",
                                    globalCode, code);
                        }
                    } else if (ObjectMapperProvider.MESSAGE.equals(tname)) {
                        messages.add(jp.nextTextValue());
                    } else if (ObjectMapperProvider.ID.equals(tname)) {
                        try {
                            id = UUID.fromString(jp.nextTextValue());
                        } catch (final IllegalArgumentException e) {
                            LOGGER.warn("Error when deserializing reason id", e);
                            // Ignore error, id wil be null and managed below
                        }
                    } // else lets just ignore usermessage and others...
                }

                reason = (null != globalCode) ?
                        (null != id) ?
                                new KasperReason(id, globalCode, messages)
                                : new KasperReason(globalCode, messages)
                        : null;

            } else if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
                jp.skipChildren();
            }

        }

        if ( ! status.equals(Status.OK) && (null == reason) ) {
            reason = new KasperReason(CoreReasonCode.UNKNOWN_REASON, "Deserialization issue of the command response");
        }

        return new CommandResponse(status, reason);
        */
    }

    public CommandResponse deserialize_new(final ObjectNode root) throws IOException {

        // ID
        final String id = root.get(ObjectMapperProvider.ID).asText();

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
        final Integer code = root.get(ObjectMapperProvider.CODE).asInt(CoreReasonCode.UNKNOWN_REASON.code());

        // LABEL
        final String label = root.get(ObjectMapperProvider.LABEL).asText();

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
                return new CommandResponse(status, new KasperReason(UUID.fromString(id), strCode, messages));
            } catch (final IllegalArgumentException e) {
                LOGGER.warn("Error when deserializing reason id", e);
                return CommandResponse.error(new KasperReason(strCode, messages));
            }
        } else {
            return new CommandResponse(status, new KasperReason(strCode, messages));
        }

    }

}
