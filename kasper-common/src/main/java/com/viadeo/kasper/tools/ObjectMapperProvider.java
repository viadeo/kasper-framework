// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;

public class ObjectMapperProvider {

    static final String ERROR = "error";
    static final String ERRORS = "errors";
    static final String MESSAGE = "message";

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
        final Module kasperClientModule = new SimpleModule()
                .addSerializer(KasperQueryException.class, new KasperQueryExceptionSerializer())
                .addDeserializer(CommandResult.class, new KasperCommandResultDeserializer())
                .addDeserializer(KasperError.class, new KasperErrorDeserializer())
                .addDeserializer(KasperQueryException.class, new KasperQueryExceptionDeserializer());
        mapper.registerModule(kasperClientModule);

        /* Third-party modules */
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JodaModule());
    }

    // ------------------------------------------------------------------------

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
