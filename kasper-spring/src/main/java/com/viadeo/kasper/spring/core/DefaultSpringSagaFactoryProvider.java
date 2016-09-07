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
package com.viadeo.kasper.spring.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultSpringSagaFactoryProvider implements SagaFactoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSpringSagaFactoryProvider.class);

    private static final Map<Class<? extends Saga>, SagaFactory> CACHE = Maps.newHashMap();

    @VisibleForTesting
    public static void clearCache() {
        CACHE.clear();
    }

    private final ApplicationContext applicationContext;

    public DefaultSpringSagaFactoryProvider(final ApplicationContext applicationContext) {
        this.applicationContext = checkNotNull(applicationContext);
    }

    @Override
    public <SAGA extends Saga> Optional<SagaFactory> get(final Class<SAGA> sagaClass) {
        return Optional.fromNullable(CACHE.get(sagaClass));
    }

    @Override
    public SagaFactory getOrCreate(final Saga saga) {
        Optional<SagaFactory> optionalSagaFactory = get(saga.getClass());

        if (optionalSagaFactory.isPresent()) {
            return optionalSagaFactory.get();
        }

        final Set<Class<?>> parameterTypes = Sets.newHashSet();

        for (final Constructor<?> constructor : saga.getClass().getConstructors()) {
            parameterTypes.addAll(Arrays.asList(constructor.getParameterTypes()));
        }

        final GenericApplicationContext sagaContext = new GenericApplicationContext();
        sagaContext.setParent(applicationContext);

        final ConfigurableListableBeanFactory beanFactory = sagaContext.getBeanFactory();

        for (final Field field : saga.getClass().getDeclaredFields()) {
            if (parameterTypes.contains(field.getType()) && applicationContext.getBeanNamesForType(field.getType()).length == 0) {
                field.setAccessible(Boolean.TRUE);
                try {
                    beanFactory.registerSingleton(field.getName(), field.get(saga));
                } catch (final IllegalAccessException e) {
                    LOGGER.warn("Failed to enrich the context with '{}'", field.getName(), e);
                }
            }
        }

        sagaContext.refresh();

        SagaFactory sagaFactory = new DefaultSpringSagaFactory(sagaContext);

        CACHE.put(saga.getClass(), sagaFactory);

        return sagaFactory;
    }
}
