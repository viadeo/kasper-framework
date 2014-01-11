// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueryResponseDeserializer extends JsonDeserializer<QueryResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperProvider.class); 

    private final JavaType responseType;

    QueryResponseDeserializer(final JavaType responseType) {
        this.responseType = responseType;
    }

    @Override
    public QueryResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {

        ObjectNode root = jp.readValueAs(ObjectNode.class);

        if (root.has(ObjectMapperProvider.REASON) && root.get(ObjectMapperProvider.REASON).asBoolean()) {

            QueryResponse.Status status = QueryResponse.Status.ERROR;
            if (root.has(ObjectMapperProvider.STATUS)) {
                try {
                    status = QueryResponse.Status.valueOf(root.get(ObjectMapperProvider.STATUS).asText());
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

}
