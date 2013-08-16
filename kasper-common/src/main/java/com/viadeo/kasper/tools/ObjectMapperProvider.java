// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ObjectMapperProvider {

    private static final String ERROR = "error";
    private static final String ERRORS = "errors";
    private static final String MESSAGE = "message";

    public static final ObjectMapperProvider INSTANCE = new ObjectMapperProvider();

    private final ObjectMapper mapper;


    // ------------------------------------------------------------------------

    static class KasperQueryExceptionSerializer extends JsonSerializer<KasperQueryException> {
        @Override
        public void serialize(final KasperQueryException value, final JsonGenerator jgen, final SerializerProvider provider)
                throws IOException {
            jgen.writeStartObject();

            // lets write a boolean telling that this is an error, can be useful for js consumers
            jgen.writeFieldName(ERROR);
            jgen.writeBoolean(true);

            jgen.writeFieldName(MESSAGE);
            jgen.writeString(value.getMessage());

            jgen.writeFieldName(ERRORS);
            jgen.writeStartArray();

            List<KasperError> emptyList = ImmutableList.of();
            for (final KasperError error : value.getErrors().or(emptyList)) {
                jgen.writeObject(error);
            }
            jgen.writeEndArray();

            jgen.writeEndObject();
        }
    }

    // ------------------------------------------------------------------------

    static class KasperQueryExceptionDeserializer extends JsonDeserializer<KasperQueryException> {
        private static final Logger LOGGER = LoggerFactory.getLogger(KasperQueryExceptionDeserializer.class);

        private final TypeReference<List<KasperError>> listOfKasperErrorType = new TypeReference<List<KasperError>>() {};

        @Override
        public KasperQueryException deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            String message = null;
            List<KasperError> errors = null;

            while ((null != jp.nextToken()) && !jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
                final String name = jp.getCurrentName();

                if (MESSAGE.equals(name)) {
                    message = jp.getValueAsString();
                } else if (ERRORS.equals(name)) {
                    jp.nextToken();
                    errors = jp.readValueAs(listOfKasperErrorType);
                } else {
                    LOGGER.warn("Unknown property[{}]", name);
                }
            }

            return new KasperQueryException(message, null, errors);
        }
    }

    // ------------------------------------------------------------------------

    private ObjectMapperProvider() {

        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_CREATORS, true);
        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
        mapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        final Module kasperClientModule = new SimpleModule()
                .addSerializer(KasperQueryException.class, new KasperQueryExceptionSerializer())
                .addDeserializer(CommandResult.class, new KasperCommandResultDeserializer())
                .addDeserializer(KasperError.class, new KasperErrorDeserializer())
                .addDeserializer(KasperQueryException.class, new KasperQueryExceptionDeserializer());

        mapper.registerModule(kasperClientModule).registerModule(new GuavaModule());

        mapper.registerModule(kasperClientModule).registerModule(new JodaModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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

    public ObjectMapper mapper() {
        return mapper;
    }

}
