// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PropertyAdapter extends TypeAdapter<Object> {
    
    private final Method accessor;
    private final String name;
    private final TypeAdapter<Object> adapter;

    // ------------------------------------------------------------------------
    
    public PropertyAdapter(final Method accessor, final String name, final TypeAdapter<Object> adapter) {
        this.accessor = checkNotNull(accessor);
        this.name = checkNotNull(name);
        this.adapter = checkNotNull(adapter);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void adapt(final Object bean, final QueryBuilder builder) {
        try {
            final Object value = accessor.invoke(bean);
            if (null != value) {
                builder.begin(name);
                adapter.adapt(value, builder);
            }
        } catch (final IllegalArgumentException e) {
            throw cannotGetPropertyValue(e);
        } catch (final IllegalAccessException e) {
            throw cannotGetPropertyValue(e);
        } catch (final InvocationTargetException e) {
            throw cannotGetPropertyValue(e);
        }
    }

    // ------------------------------------------------------------------------
    
    private KasperClientException cannotGetPropertyValue(final Exception e) {
        return new KasperClientException("Unable to get value of property " + name
                + " from bean " + accessor.getDeclaringClass(), e);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        
        final PropertyAdapter other = (PropertyAdapter) obj;
        if (null == name) {
            if (null != other.name) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        
        return true;
    }
    
}