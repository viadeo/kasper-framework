// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KasperResponseModule extends SimpleModule {

    public KasperResponseModule() {
        super();
        setDeserializers(new CommandQueryResponseDeserializerAdapter());

        addSerializer(QueryResponse.class, new QueryResponseSerializer());

        addSerializer(CommandResponse.class, new CommandResponseSerializer());
        addDeserializer(CommandResponse.class, new CommandResponseDeserializer());

        addDeserializer(KasperReason.class, new KasperReasonDeserializer());
    }

    // ------------------------------------------------------------------------

    public static class CommandResponseSerializer extends KasperResponseSerializer<CommandResponse> {

    /* Uses the standard serializer */

    }

    // ------------------------------------------------------------------------

    public static final class CommandResponseDeserializer extends KasperResponseDeserializer<CommandResponse> {
        static final Logger LOGGER = LoggerFactory.getLogger(CommandResponseDeserializer.class);


        public CommandResponseDeserializer() { }


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

        public CommandResponse deserialize_old(final ObjectNode root) throws IOException {
            KasperResponse.Status status = KasperResponse.Status.ERROR;

            if (root.has(ObjectMapperProvider.STATUS)) {
                try {
                    status = KasperResponse.Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
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
                if ( ! status.equals(KasperResponse.Status.OK)) {
                    reason = new KasperReason(globalCode, messages);
                }
                return new CommandResponse(status, reason);

            }

        }

        public CommandResponse deserialize_new(final ObjectNode root) throws IOException {
            final KasperResponse kasperResponse = super.deserialize(root);
            return new CommandResponse(kasperResponse);
        }

    }

    // ------------------------------------------------------------------------

    public static class CommandQueryResponseDeserializerAdapter extends SimpleDeserializers {
        private static final long serialVersionUID = 1995270375280248186L;

        @Override
        public JsonDeserializer findBeanDeserializer(final JavaType type,
                                                     final DeserializationConfig config,
                                                     final BeanDescription beanDesc)
                throws JsonMappingException {

            if (type.hasRawClass(QueryResponse.class)) {
                return new QueryResponseDeserializer(type.containedType(0));

            } else if (type.hasRawClass(CommandResponse.class)) {
                return new CommandResponseDeserializer();

            } else {
                return super.findBeanDeserializer(type, config, beanDesc);
            }
        }

    }

    // ------------------------------------------------------------------------

    public static class QueryResponseSerializer extends KasperResponseSerializer<QueryResponse> {

        @Override
        public void serialize(final QueryResponse value, final JsonGenerator jgen, final SerializerProvider provider)
                throws IOException {

            if ( ! value.isOK()) {
                super.serialize(value, jgen, provider);
            } else {
                jgen.writeObject(value.getResult());
            }
        }
    }

    // ------------------------------------------------------------------------

    public static class KasperReasonDeserializer extends JsonDeserializer<KasperReason> {

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

    // ------------------------------------------------------------------------

    public static abstract class KasperResponseDeserializer<R extends KasperResponse> extends JsonDeserializer<R> {
        private static final Logger LOGGER = LoggerFactory.getLogger(KasperResponseDeserializer.class);


        private final KasperResponseModule.KasperReasonDeserializer kasperReasonDeserializer;

        protected KasperResponseDeserializer() {
            this.kasperReasonDeserializer = new KasperResponseModule.KasperReasonDeserializer();
        }

        protected KasperResponse deserialize(final ObjectNode root) throws IOException {
            // STATUS
            KasperResponse.Status status = KasperResponse.Status.ERROR;
            if (root.has(ObjectMapperProvider.STATUS)) {
                try {
                    status = KasperResponse.Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
                } catch (final IllegalArgumentException e) {
                    LOGGER.error("Unable to determine status", e);
                }
            }

            KasperReason kasperReason = kasperReasonDeserializer.deserialize(root);

            // TODO: add Security Token
            return new KasperResponse(status, kasperReason);

        }

    }

    // ------------------------------------------------------------------------

    public static abstract class KasperResponseSerializer<R extends KasperResponse> extends JsonSerializer<R> {

        @Override
        public void serialize(final R value, final JsonGenerator jgen, final SerializerProvider provider)
                throws IOException {
            start(jgen);
            body(value, jgen, provider);
            end(jgen);
        }

        protected void start(final JsonGenerator jgen) throws IOException {
            jgen.writeStartObject();
        }

        protected void body(final R value, final JsonGenerator jgen, final SerializerProvider provider)
                throws IOException {

            jgen.writeStringField(ObjectMapperProvider.STATUS, value.getStatus().name());

            final KasperReason reason = value.getReason();
            if (null != reason) {
                jgen.writeStringField(ObjectMapperProvider.ID, reason.getId().toString());
                jgen.writeNumberField(ObjectMapperProvider.CODE, reason.getReasonCode());
                jgen.writeStringField(ObjectMapperProvider.LABEL, reason.getLabel());
            }

            jgen.writeFieldName(ObjectMapperProvider.REASON);
            jgen.writeBoolean( ! value.isOK());

            jgen.writeFieldName(ObjectMapperProvider.REASONS);
            jgen.writeStartArray();
            if ( ! value.isOK()) {
                for (String message : reason.getMessages()) {
                    jgen.writeStartObject();
                    jgen.writeStringField(ObjectMapperProvider.MESSAGE, message);
                    jgen.writeEndObject();
                }
            }
            jgen.writeEndArray();
        }

        protected void end(final JsonGenerator jgen) throws IOException {
            jgen.writeEndObject();
        }

    }

    // ------------------------------------------------------------------------

    public static class QueryResponseDeserializer extends KasperResponseDeserializer<QueryResponse> {

        private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperProvider.class);

        private final JavaType responseType;

        QueryResponseDeserializer(final JavaType responseType) {
            this.responseType = responseType;
        }

        @Override
        public QueryResponse deserialize(final JsonParser jp, final DeserializationContext ctxt)
                throws IOException {

            final ObjectNode root = jp.readValueAs(ObjectNode.class);

            if (root.has(ObjectMapperProvider.ID)) {
                return deserialize_new(jp, root);
            } else {
                return deserialize_old(jp, root);
            }
        }

        public QueryResponse deserialize_old(final JsonParser jp, final ObjectNode root)
                throws IOException {

            if (root.has(ObjectMapperProvider.REASON) && root.get(ObjectMapperProvider.REASON).asBoolean()) {

                KasperResponse.Status status = KasperResponse.Status.ERROR;
                if (root.has(ObjectMapperProvider.STATUS)) {
                    try {
                        status = KasperResponse.Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
                    } catch (final IllegalArgumentException e) {
                        LOGGER.error("Unable to determine status", e);
                    }
                }

                String id = null;
                final String globalCode = root.get(ObjectMapperProvider.MESSAGE).asText();
                final List<String> messages = new ArrayList<String>();
                for (final JsonNode node : root.get(ObjectMapperProvider.REASONS)) {
                    id = node.get(ObjectMapperProvider.ID).asText();
                    final String code = node.get(ObjectMapperProvider.CODE).asText();
                    final String message = node.get(ObjectMapperProvider.MESSAGE).asText();
                    if (globalCode.equals(code)) {
                        messages.add(message);
                    } else {
                        LOGGER.warn("Global code[{}] does not match error code[{}] with message[{}]",
                                globalCode, code, message);
                    }
                }

                if (null != id) {
                    try {
                        return new QueryResponse(status, new KasperReason(UUID.fromString(id), globalCode, messages));
                    } catch (final IllegalArgumentException e) {
                        LOGGER.warn("Error when deserializing reason id", e);
                        return QueryResponse.error(new KasperReason(globalCode, messages));
                    }
                } else {
                    return new QueryResponse(status, new KasperReason(globalCode, messages));
                }

            } else {
                // not very efficient but will be fine for now
                return QueryResponse.of((QueryResult) ((ObjectMapper) jp.getCodec()).convertValue(root, responseType));
            }
        }

        public QueryResponse deserialize_new(final JsonParser jp, final ObjectNode root)
                throws IOException {

            if (root.has(ObjectMapperProvider.REASON) && root.get(ObjectMapperProvider.REASON).asBoolean()) {

                final KasperResponse kasperResponse = super.deserialize(root);
                return new QueryResponse(kasperResponse);

            } else {
                // not very efficient but will be fine for now
                return QueryResponse.of((QueryResult) ((ObjectMapper) jp.getCodec()).convertValue(root, responseType));
            }
        }

    }

    // ------------------------------------------------------------------------
}
