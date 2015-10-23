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
import com.viadeo.kasper.spring.core.DefaultSpringSagaFactory;
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
import static org.mockito.Matchers.any;
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
