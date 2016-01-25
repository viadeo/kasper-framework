// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Map.Entry;

/**
 * Mapper which converts Saga into Map and conversely.
 */
public class SagaMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaMapper.class);

    public static final String X_KASPER_SAGA_CLASS = "X-KASPER-SAGA-CLASS";
    public static final String X_KASPER_SAGA_IDENTIFIER = "X-KASPER-SAGA-IDENTIFIER";

    private static final Map<Class,MappingDescriptor> MAPPING_DESCRIPTOR_BY_SAGA_CLASS = Maps.newHashMap();

    private final ObjectMapper mapper;
    private final SagaFactoryProvider sagaFactoryProvider;

    // ------------------------------------------------------------------------

    public SagaMapper(final SagaFactoryProvider sagaFactoryProvider) {
        this(sagaFactoryProvider, ObjectMapperProvider.INSTANCE.mapper());
    }

    public SagaMapper(final SagaFactoryProvider sagaFactoryProvider, final ObjectMapper mapper) {
        this.mapper = checkNotNull(mapper);
        this.sagaFactoryProvider = checkNotNull(sagaFactoryProvider);
    }

    // ------------------------------------------------------------------------

    public <SAGA extends Saga> SAGA to(final Class<SAGA> sagaClass, final Object identifier, final Map<String, String> props) {
        final Map<String, String> properties = Maps.newHashMap(props);
        properties.remove(X_KASPER_SAGA_CLASS);
        properties.remove(X_KASPER_SAGA_IDENTIFIER);

        final Optional<SagaFactory> optionalSagaFactory = sagaFactoryProvider.get(sagaClass);

        if ( ! optionalSagaFactory.isPresent()) {
            throw new SagaInstantiationException(String.format("No related saga factory : %s", sagaClass.getName()));
        }

        final SAGA saga = optionalSagaFactory.get().create(identifier, sagaClass);
        final MappingDescriptor mappingDescriptor = getOrCreateMappingDescriptor(saga.getClass());

        for (final Entry<String, String> entry : properties.entrySet()) {
            final Optional<Field> fieldOptional = mappingDescriptor.getFieldByName(entry.getKey());

            if (fieldOptional.isPresent()) {
                try {
                    final Field field = fieldOptional.get();
                    field.set(saga, mapper.readValue(entry.getValue(), field.getType()));

                } catch (final IllegalAccessException | IOException e) {
                    LOGGER.error("Failed to restore property '{}' to a saga instance, <saga={}> <identifier={}> <propertyValue={}>",
                            entry.getKey(), saga.getClass(), identifier, entry.getValue(), e);
                }
            } else {
                LOGGER.error("Failed to restore property '{}' to a saga instance, <saga={}> <identifier={}> <propertyValue={}>",
                        entry.getKey(), saga.getClass(), identifier, entry.getValue());
            }
        }

        return saga;
    }

    public Map<String, String> from(final Object identifier, final Saga saga) {
        checkNotNull(saga);

        final Map<String, String> properties = Maps.newHashMap();
        properties.put(X_KASPER_SAGA_CLASS, saga.getClass().getName());

        try {
            properties.put(X_KASPER_SAGA_IDENTIFIER, mapper.writeValueAsString(identifier));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize property '{}' from a saga, <saga={}> <identifier={}>",
                    X_KASPER_SAGA_IDENTIFIER, saga.getClass(), identifier, e);
        }

        final MappingDescriptor mappingDescriptor = getOrCreateMappingDescriptor(saga.getClass());

        for (final Field field : mappingDescriptor.getFields()) {
            Object value = null;
            try {
                value = field.get(saga);
            } catch (final IllegalAccessException e) {
                LOGGER.error("Failed to extract property '{}' from a saga, <saga={}> <identifier={}>",
                        field.getName(), saga.getClass(), identifier, e);
            }

            if (null != value) {
                try {
                    properties.put(field.getName(), mapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Failed to serialize property '{}' from a saga, <saga={}> <identifier={}>",
                            field.getName(), saga.getClass(), identifier, e);
                }
            }
        }

        return properties;
    }

    public MappingDescriptor getOrCreateMappingDescriptor(final Class<? extends Saga> sagaClass) {
        final List<Field> fields = Lists.newArrayList();

        MappingDescriptor mappingDescriptor = MAPPING_DESCRIPTOR_BY_SAGA_CLASS.get(sagaClass);

        if (mappingDescriptor == null) {
            for (final Field field : sagaClass.getDeclaredFields()) {
                field.setAccessible(Boolean.TRUE);

                if ( ! field.getName().startsWith("$") && (Serializable.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive())) {
                    fields.add(field);
                }
            }

            mappingDescriptor = new MappingDescriptor(sagaClass, fields);
            MAPPING_DESCRIPTOR_BY_SAGA_CLASS.put(sagaClass, mappingDescriptor);
        }

        return mappingDescriptor;
    }

    // ------------------------------------------------------------------------

    private static class MappingDescriptor {

        private final Class reference;
        private final Map<String,Field> fieldsByName;

        private MappingDescriptor(Class reference, List<Field> fields) {
            this.reference = reference;
            this.fieldsByName = Maps.newHashMap();
            for (Field field : fields) {
                this.fieldsByName.put(field.getName(), field);
            }
        }

        private Class getReference() {
            return reference;
        }

        private Optional<Field> getFieldByName(final String name) {
            return Optional.fromNullable(fieldsByName.get(name));
        }

        private List<Field> getFields() {
            return Lists.newArrayList(fieldsByName.values());
        }
    }
}
