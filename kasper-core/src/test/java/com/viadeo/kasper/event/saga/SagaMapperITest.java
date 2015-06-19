package com.viadeo.kasper.event.saga;

import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SagaMapperITest {

    private SagaMapper sagaMapper;
    private DefaultSagaFactory sagaFactory;

    @Before
    public void setUp() throws Exception {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("commandGateway", mock(KasperCommandGateway.class));

        sagaFactory = new DefaultSagaFactory(applicationContext);
        sagaMapper = new SagaMapper(sagaFactory);
    }

    @Test
    public void from() throws Exception {
        // Given
        UUID identifier = UUID.randomUUID();

        TestFixture.TestSagaB saga = sagaFactory.create(identifier, TestFixture.TestSagaB.class);
        saga.setCount(666);
        saga.setName("Chuck");

        // When
        Map<String,Object> properties = sagaMapper.from(identifier, saga);

        // Then
        assertNotNull(properties);
        assertEquals(4, properties.size());
        assertEquals(identifier, properties.get(SagaMapper.X_KASPER_SAGA_IDENTIFIER));
        assertEquals(saga.getClass(), properties.get(SagaMapper.X_KASPER_SAGA_CLASS));
        assertEquals(666, properties.get("count"));
        assertEquals("Chuck", properties.get("name"));

    }

    @Test
    public void to() throws Exception {
        // Given
        UUID identifier = UUID.randomUUID();

        Map<String,Object> properties = Maps.newHashMap();
        properties.put(SagaMapper.X_KASPER_SAGA_IDENTIFIER, identifier);
        properties.put(SagaMapper.X_KASPER_SAGA_CLASS, TestFixture.TestSagaB.class);
        properties.put("count", 666);
        properties.put("name", "Chuck");

        // When
        Saga saga = sagaMapper.to(properties);

        // Then
        assertNotNull(saga);
        assertTrue(saga instanceof TestFixture.TestSagaB);

        TestFixture.TestSagaB testSagaB = (TestFixture.TestSagaB) saga;

        assertEquals("Chuck", testSagaB.getName());
        assertEquals(666, testSagaB.getCount());
        assertNotNull(testSagaB.getCommandGateway());

    }
}
