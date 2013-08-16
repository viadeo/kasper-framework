// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class BeanConstructor {
    private final Constructor<Object> ctr;
    private final Map<String, BeanConstructorProperty> parameters;

    public BeanConstructor(final Constructor<Object> ctr,
                           final Map<String, BeanConstructorProperty> parameters) {
        this.ctr = ctr;
        this.parameters = parameters;
    }

    public Object create(final Object[] params) {
        try {

            return ctr.newInstance(params);

        } catch (final IllegalArgumentException e) {
            throw couldNotInstanciateQuery(e);
        } catch (final InstantiationException e) {
            throw couldNotInstanciateQuery(e);
        } catch (final IllegalAccessException e) {
            throw couldNotInstanciateQuery(e);
        } catch (final InvocationTargetException e) {
            throw couldNotInstanciateQuery(e);
        }
    }

    private KasperQueryAdapterException couldNotInstanciateQuery(final Exception e) {
        return new KasperQueryAdapterException("Failed to instanciate query of type "
                + ctr.getDeclaringClass(), e);
    }

    public Map<String, BeanConstructorProperty> parameters() {
        return parameters;
    }

}
