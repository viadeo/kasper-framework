// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;

public class ObjectMapperProvider {
    private static final String ERROR = "error";
    private static final String ERRORS = "errors";
    private static final String MESSAGE = "message";

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

    public static final ObjectMapperProvider instance = new ObjectMapperProvider();

    private final ObjectWriter writer;
    private final ObjectReader reader;
    private final ObjectMapper mapper;

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

        final Module kasperClientModule = new SimpleModule()
                .addSerializer(KasperQueryException.class, new KasperQueryExceptionSerializer())
                .addDeserializer(CommandResult.class, new KasperCommandResultDeserializer())
                .addDeserializer(KasperError.class, new KasperErrorDeserializer())
                .addDeserializer(KasperQueryException.class, new KasperQueryExceptionDeserializer());

        mapper.registerModule(kasperClientModule).registerModule(new GuavaModule());

        writer = mapper.writer();
        reader = mapper.reader();
    }

    // ------------------------------------------------------------------------

    /**
     * @return the configured instance of ObjectWriter to use. This writer is shared between server and client code thus
     * do not reconfigure it.
     */
    public ObjectWriter objectWriter() {
        return writer;
    }

    /**
     * @return the configured instance of ObjectReader to use. This reader is shared between server and client code thus
     * do not reconfigure it.
     */
    public ObjectReader objectReader() {
        return reader;
    }

    /**
     * @return this instance should not be modified.
     */
    public ObjectMapper mapper() {
        return mapper;
    }

}
