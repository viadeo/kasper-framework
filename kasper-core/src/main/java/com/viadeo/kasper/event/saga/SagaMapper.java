// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Map.Entry;

public class SagaMapper {

    public static final String X_KASPER_SAGA_CLASS = "X-KASPER-SAGA-CLASS";
    public static final String X_KASPER_SAGA_IDENTIFIER = "X-KASPER-SAGA-IDENTIFIER";

    private SagaFactory sagaFactory;

    // ------------------------------------------------------------------------

    public SagaMapper(final SagaFactory sagaFactory) {
        this.sagaFactory = checkNotNull(sagaFactory);
    }

    // ------------------------------------------------------------------------

    public Saga to(final Map<String, Object> props) {
        final Map<String, Object> properties = Maps.newHashMap(props);
        final Class sagaClass = (Class) properties.remove(X_KASPER_SAGA_CLASS);
        final Object identifier = properties.remove(X_KASPER_SAGA_IDENTIFIER);

        final Saga saga = sagaFactory.create(identifier, sagaClass);

        for (final Entry<String, Object> entry : properties.entrySet()) {
            try {
                final Field field = sagaClass.getDeclaredField(entry.getKey());
                field.setAccessible(Boolean.TRUE);
                field.set(saga, entry.getValue());
            } catch (final IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                // FIXME TODO
            }
        }

        return saga;
    }

    public Map<String, Object> from(final Object identifier, final Saga saga) {
        checkNotNull(saga);

        final Map<String, Object> properties = Maps.newHashMap();
        properties.put(X_KASPER_SAGA_CLASS, saga.getClass());
        properties.put(X_KASPER_SAGA_IDENTIFIER, identifier);

        for (final Field field : saga.getClass().getDeclaredFields()) {
            field.setAccessible(Boolean.TRUE);

            if ( ! field.getName().startsWith("$") && (Serializable.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive())) {
                Object value = null;
                try {
                    value = field.get(saga);
                } catch (final IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    // FIXME TODO
                }

                if (null != value) {
                    properties.put(field.getName(), value);
                }
            }
        }

        return properties;
    }

}
