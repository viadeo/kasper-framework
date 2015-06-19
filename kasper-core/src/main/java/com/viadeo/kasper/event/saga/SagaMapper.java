package com.viadeo.kasper.event.saga;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaMapper {

    public static final String X_KASPER_SAGA_CLASS = "X-KASPER-SAGA-CLASS";
    public static final String X_KASPER_SAGA_IDENTIFIER = "X-KASPER-SAGA-IDENTIFIER";

    private SagaFactory sagaFactory;

    public SagaMapper(SagaFactory sagaFactory) {
        this.sagaFactory = sagaFactory;
    }

    public Saga to(Map<String, Object> props) {
        Map<String, Object> properties = Maps.newHashMap(props);
        Class sagaClass = (Class) properties.remove(X_KASPER_SAGA_CLASS);
        Object identifier = properties.remove(X_KASPER_SAGA_IDENTIFIER);

        Saga saga = sagaFactory.create(identifier, sagaClass);

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            try {
                Field field = sagaClass.getDeclaredField(entry.getKey());
                field.setAccessible(Boolean.TRUE);
                field.set(saga, entry.getValue());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return saga;
    }

    public Map<String, Object> from(Object identifier, Saga saga) {
        checkNotNull(saga);

        Map<String, Object> properties = Maps.newHashMap();
        properties.put(X_KASPER_SAGA_CLASS, saga.getClass());
        properties.put(X_KASPER_SAGA_IDENTIFIER, identifier);

        for (Field field : saga.getClass().getDeclaredFields()) {
            field.setAccessible(Boolean.TRUE);

            if (Serializable.class.isAssignableFrom(field.getType()) || field.getType().isPrimitive()) {
                Object value = null;
                try {
                    value = field.get(saga);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                if (value != null) {
                    properties.put(field.getName(), value);
                }
            }
        }

        return properties;
    }
}
