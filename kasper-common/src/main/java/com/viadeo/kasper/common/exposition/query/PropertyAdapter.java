// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.exception.KasperQueryAdapterException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class PropertyAdapter {

    private static final Map<Class, Object> DEFAULT_VALUES_FOR_PRIMITIVES = Maps.newHashMap();
    {
        DEFAULT_VALUES_FOR_PRIMITIVES.put(int.class, 0);
        DEFAULT_VALUES_FOR_PRIMITIVES.put(double.class, 0d);
        DEFAULT_VALUES_FOR_PRIMITIVES.put(long.class, 0l);
        DEFAULT_VALUES_FOR_PRIMITIVES.put(short.class, 0);
        DEFAULT_VALUES_FOR_PRIMITIVES.put(float.class, 0f);
        DEFAULT_VALUES_FOR_PRIMITIVES.put(boolean.class, false);
    }

    private final Method mutator;
    private final Method accessor;
    private final BeanProperty property;
    private final TypeAdapter<Object> adapter;

    // it is a bit ugly to use it to distinguish between typeadapter and beanadapter, 
    // but it does the work with less code & anyway PropertyAdapter is an internal class 
    private final boolean handleName;

    // ------------------------------------------------------------------------

    public PropertyAdapter(final BeanProperty property,
                           final Method accessor,
                           final Method mutator,
                           final TypeAdapter<Object> adapter,
                           final boolean handleName)
    {
        this.property = checkNotNull(property);
        this.accessor = checkNotNull(accessor);

        this.mutator = mutator; /* null allowed as could be a ctr param */
        this.adapter = checkNotNull(adapter);

        if (null != mutator) {
            this.mutator.setAccessible(true);
        }

        this.accessor.setAccessible(true);
        this.handleName = handleName;
    }

    // ------------------------------------------------------------------------

    public void adapt(final Object bean, final QueryBuilder builder) throws Exception {
        try {
            final Object value = accessor.invoke(bean);
            if (handleName) {
                builder.begin(property.getName());
                adapter.adapt(value, builder);
                builder.end();
            } else {
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

    public Object adapt(final QueryParser parser) throws Exception {
        final Class rawClass = property.getTypeToken().getRawType();

        /*
         * ok it is ugly but for the moment we have to do that in order to
         * support composed queries (in fact the problem is more general: we try
         * to support non flat objects, meaning that a type can be adapted into
         * multiple key/value pairs but the representation language (url query)
         * does not have such thing).
         */

        if (Query.class.isAssignableFrom(rawClass) || !handleName) {

            return adapter.adapt(parser);

        } else if (parser.exists(property.getName())) {

            // in fact in most cases we will do that
            parser.begin(property.getName());

            final Object value = adapter.adapt(parser);
            parser.end();
            return value;

        } else if (rawClass.isPrimitive()) {

            return DEFAULT_VALUES_FOR_PRIMITIVES.get(rawClass);

        } else {
            return null;
        }
    }

    public void mutate(final Object bean, final Object value) {
        try {

            mutator.invoke(bean, value);

        } catch (final IllegalArgumentException e) {
            throw cannotSetPropertyValue(bean, e);
        } catch (final IllegalAccessException e) {
            throw cannotSetPropertyValue(bean, e);
        } catch (final InvocationTargetException e) {
            throw cannotSetPropertyValue(bean, e);
        }
    }
    
    public boolean existsInQuery(QueryParser parser) {
        return parser.exists(getName()) || ( ! handleName);
    }

    // ------------------------------------------------------------------------

    private KasperQueryAdapterException cannotGetPropertyValue(final Exception e) {
        return new KasperQueryAdapterException(
            String.format(
                "Unable to get value of property %s from bean %s",
                property.getName(),
                accessor.getDeclaringClass()
            ),
            e
        );
    }

    private KasperQueryAdapterException cannotSetPropertyValue(final Object value, final Exception e) {
        return new KasperQueryAdapterException(
                String.format(
                    "Unable to set property %s from bean %s with value %s",
                    property.getName(),
                    accessor.getDeclaringClass(),
                    value
                ),
                e
        );
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return property.getName();
    }

}
