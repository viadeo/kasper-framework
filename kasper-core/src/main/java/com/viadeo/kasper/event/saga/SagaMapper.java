// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
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

    private final ObjectMapper mapper;
    private final SagaFactory sagaFactory;

    // ------------------------------------------------------------------------

    public SagaMapper(final SagaFactory sagaFactory) {
        this(sagaFactory, ObjectMapperProvider.INSTANCE.mapper());
    }

    public SagaMapper(final SagaFactory sagaFactory, final ObjectMapper mapper) {
        this.mapper = checkNotNull(mapper);
        this.sagaFactory = checkNotNull(sagaFactory);
    }

    // ------------------------------------------------------------------------

    public <SAGA extends Saga> SAGA to(final Class<SAGA> sagaClass, final Object identifier, final Map<String, String> props) {
        final Map<String, String> properties = Maps.newHashMap(props);
        properties.remove(X_KASPER_SAGA_CLASS);
        properties.remove(X_KASPER_SAGA_IDENTIFIER);

        final SAGA saga = sagaFactory.create(identifier, sagaClass);

        for (final Entry<String, String> entry : properties.entrySet()) {
            try {
                final Field field = sagaClass.getDeclaredField(entry.getKey());
                field.setAccessible(Boolean.TRUE);
                field.set(saga, mapper.readValue(entry.getValue(), field.getType()));

            } catch (final IllegalAccessException | NoSuchFieldException | IOException e) {
                LOGGER.error("Failed to restore property '{}' to a saga instance, <saga={}> <identifier={}> <propertyValue={}>",
                        entry.getKey(), saga.getClass(), identifier, entry.getValue(), e);
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

        for (final Field field : saga.getClass().getDeclaredFields()) {
            field.setAccessible(Boolean.TRUE);

            if ( ! field.getName().startsWith("$") && (Serializable.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive())) {
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
        }

        return properties;
    }

}
