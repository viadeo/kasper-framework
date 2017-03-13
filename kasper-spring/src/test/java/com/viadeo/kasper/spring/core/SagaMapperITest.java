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

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaMapper;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.factory.DefaultSagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SagaMapperITest {

    private SagaMapper sagaMapper;
    private DefaultSagaFactory sagaFactory;

    @Before
    public void setUp() throws Exception {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("commandGateway", mock(KasperCommandGateway.class));

        sagaFactory = new DefaultSpringSagaFactory(applicationContext);

        SagaFactoryProvider sagaFactoryProvider = mock(SagaFactoryProvider.class);
        when(sagaFactoryProvider.get(any(Class.class))).thenReturn(Optional.<SagaFactory>of(sagaFactory));
        when(sagaFactoryProvider.getOrCreate(any(Saga.class))).thenReturn(sagaFactory);


        sagaMapper = new SagaMapper(sagaFactoryProvider);
    }

    @Test
    public void from() throws Exception {
        // Given
        UUID identifier = UUID.randomUUID();

        TestFixture.TestSagaB saga = sagaFactory.create(identifier, TestFixture.TestSagaB.class);
        saga.setCount(666);
        saga.setName("Chuck");

        // When
        Map<String,String> properties = sagaMapper.from(identifier, saga);

        // Then
        assertNotNull(properties);
        assertEquals("->" + properties, 5, properties.size());
        assertEquals("\"" + identifier + "\"", properties.get(SagaMapper.X_KASPER_SAGA_IDENTIFIER));
        assertEquals(saga.getClass().getName(), properties.get(SagaMapper.X_KASPER_SAGA_CLASS));
        assertEquals("666", properties.get("count"));
        assertEquals("\"Chuck\"", properties.get("name"));
        assertEquals("0", properties.get("invokedMethodCount"));

    }

    @Test
    public void to() throws Exception {
        // Given
        UUID identifier = UUID.randomUUID();
        Class<TestFixture.TestSagaB> sagaClass = TestFixture.TestSagaB.class;

        Map<String,String> properties = Maps.newHashMap();
        properties.put(SagaMapper.X_KASPER_SAGA_CLASS, sagaClass.getName());
        properties.put("count", "666");
        properties.put("name", "\"Chuck\"");

        // When
        TestFixture.TestSagaB saga = sagaMapper.to(sagaClass, identifier, properties);

        // Then
        assertNotNull(saga);
        assertEquals("Chuck", saga.getName());
        assertEquals(666, saga.getCount());
        assertNotNull(saga.getCommandGateway());

    }
}
