// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.query;

import com.viadeo.kasper.query.exposition.exception.KasperQueryAdapterException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class BeanConstructor {

    private final Constructor<Object> ctr;
    private final Map<String, BeanConstructorProperty> parameters;

    // ------------------------------------------------------------------------

    public BeanConstructor(final Constructor<Object> ctr,
                           final Map<String, BeanConstructorProperty> parameters) {
        this.ctr = checkNotNull(ctr);
        this.parameters = checkNotNull(parameters);
    }

    // ------------------------------------------------------------------------

    public Object create(final Object[] params) {
        try {

            return ctr.newInstance(params);

        } catch (final IllegalArgumentException e) {
            throw couldNotInstantiateQuery(e);
        } catch (final InstantiationException e) {
            throw couldNotInstantiateQuery(e);
        } catch (final IllegalAccessException e) {
            throw couldNotInstantiateQuery(e);
        } catch (final InvocationTargetException e) {
            throw couldNotInstantiateQuery(e);
        }
    }

    private KasperQueryAdapterException couldNotInstantiateQuery(final Exception e) {
        return new KasperQueryAdapterException(
            "Failed to instantiate query of type " + ctr.getDeclaringClass(),
            e
        );
    }

    public Map<String, BeanConstructorProperty> parameters() {
        return parameters;
    }

}
