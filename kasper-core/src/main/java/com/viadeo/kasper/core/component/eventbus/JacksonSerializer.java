// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.axonframework.serializer.*;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper;
    private final ConverterFactory converterFactory;

    public JacksonSerializer(final ObjectMapper objectMapper) {
        this.objectMapper = checkNotNull(objectMapper);
        this.converterFactory = new ChainingConverterFactory();
    }

    @Override
    public <T> SerializedObject<T> serialize(final Object object, final Class<T> expectedRepresentation) {
        checkNotNull(object);
        checkNotNull(expectedRepresentation);

        try {
            final T convertedObject = convert(byte[].class, expectedRepresentation, objectMapper.writeValueAsBytes(object));
            return new SimpleSerializedObject<>(convertedObject, expectedRepresentation, typeForClass(object.getClass()));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    protected <S, T> T convert(final Class<S> sourceType, final Class<T> targetType, final S source) {
        return getConverterFactory().getConverter(sourceType, targetType).convert(source);
    }

    @Override
    public <T> boolean canSerializeTo(final Class<T> expectedRepresentation) {
        return objectMapper.canSerialize(checkNotNull(expectedRepresentation));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T deserialize(final SerializedObject<S> serializedObject) {
        checkNotNull(serializedObject);

        try {
            final InputStream serializedData = convert(serializedObject.getContentType(), InputStream.class, serializedObject.getData());
            final Class<?> clazz = Class.forName(serializedObject.getType().getName());
            return (T) objectMapper.readValue(serializedData, clazz);
        } catch (IOException | ClassNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public Class classForType(final SerializedType type) {
        try {
            return Class.forName(type.getName());
        } catch (ClassNotFoundException e) {
            throw new UnknownSerializedTypeException(type, e);
        }
    }

    @Override
    public SerializedType typeForClass(final Class type) {
        checkNotNull(type);
        return new SimpleSerializedType(type.getName(), "0");
    }

    @Override
    public ConverterFactory getConverterFactory() {
        return converterFactory;
    }
}