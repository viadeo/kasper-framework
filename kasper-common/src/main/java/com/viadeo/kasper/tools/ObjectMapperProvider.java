// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.QueryResult;

public class ObjectMapperProvider {

    static final String ERROR = "error";
    static final String ERRORS = "errors";
    static final String MESSAGE = "message";
    static final String CODE = "code";
    static final String USERMESSAGE = "userMessage";
    static final String STATUS = "status";
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectMapperProvider.class); 

    public static final ObjectMapperProvider INSTANCE = new ObjectMapperProvider();

    private final ObjectMapper mapper;

    // ------------------------------------------------------------------------

    private ObjectMapperProvider() {
        mapper = new ObjectMapper();

        /* Generic features */
        mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
        mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);

        /* Serialization features */
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        /* De-Serialization features */
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /* Change visibility of properties if needed */
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        /* Register a specific module for Kasper Ser/Deser */
        final SimpleModule kasperClientModule = new SimpleModule()
                .addSerializer(CommandResult.class, new CommandResultSerializer())
                .addDeserializer(CommandResult.class, new CommandResultDeserializer())
                .addSerializer(QueryResult.class, new QueryResultSerializer());

        kasperClientModule.setDeserializers(new SimpleDeserializers() {
            private static final long serialVersionUID = 1995270375280248186L;

            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                    DeserializationConfig config, BeanDescription beanDesc)
                    throws JsonMappingException {
                if (type.hasRawClass(QueryResult.class)) {
                    return new QueryResultDeserializer(type.containedType(0));
                } else if (type.hasRawClass(CommandResult.class)) {
                    return new CommandResultDeserializer();
                } else
                    return super.findBeanDeserializer(type, config, beanDesc);
            }
        });

        mapper.registerModule(kasperClientModule);

        /* Third-party modules */
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JodaModule());
    }

    // ------------------------------------------------------------------------

    static KasperError translateOldErrorToKasperError(ObjectNode root) {
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
        return new KasperError(globalCode, messages);
    }
    
    /**
     * @return the configured instance of ObjectWriter to use.
     */
    public ObjectWriter objectWriter() {
        return mapper.writer();
    }

    /**
     * @return the configured instance of ObjectReader to use.
     */
    public ObjectReader objectReader() {
        return mapper.reader();
    }

    /**
     * @return the configured mapper
     */
    public ObjectMapper mapper() {
        return mapper;
    }

}
