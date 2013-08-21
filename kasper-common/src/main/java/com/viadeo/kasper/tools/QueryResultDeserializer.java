package com.viadeo.kasper.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.query.QueryResult;

public class QueryResultDeserializer extends JsonDeserializer<QueryResult<Object>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperProvider.class); 

    private final JavaType resultType;

    QueryResultDeserializer(final JavaType resultType) {
        this.resultType = resultType;
    }

    @Override
    public QueryResult<Object> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        ObjectNode root = jp.readValueAs(ObjectNode.class);

        if (root.has(ObjectMapperProvider.ERROR)) {
            String globalCode = root.get(ObjectMapperProvider.MESSAGE).asText();
            List<String> messages = new ArrayList<String>();
            for (JsonNode node : root.get(ObjectMapperProvider.ERRORS)) {
                String code = node.get(ObjectMapperProvider.CODE).asText();
                String message = node.get(ObjectMapperProvider.MESSAGE).asText();
                if (globalCode.equals(code)) {
                    messages.add(message);
                } else {
                    LOGGER.warn("Global code[{}] does not match error code[{}] with message[{}]",
                            globalCode, code, message);
                }
            }
            return QueryResult.of(new KasperError(globalCode, messages));
        } else {
            // not very efficient but will be fine for now
            return QueryResult.of(((ObjectMapper) jp.getCodec()).convertValue(root, resultType));
        }
    }

}
