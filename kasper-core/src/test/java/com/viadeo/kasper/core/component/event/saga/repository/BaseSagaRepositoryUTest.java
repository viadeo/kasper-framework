package com.viadeo.kasper.core.component.event.saga.repository;

import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaMapper;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseSagaRepositoryUTest {

    private BaseSagaRepository baseSagaRepository;

    @Mock
    private KasperCommandGateway commandGateway;

    @Mock
    private SagaMapper sagaMapper;

    @Before
    public void setUp(){
        baseSagaRepository = Mockito.spy(new BaseSagaRepository(sagaMapper) {

            @Override
            public Map<String, String> doLoad(Class<? extends Saga> sagaClass, Object identifier) throws SagaPersistenceException {
                return null;
            }

            @Override
            public void doSave(Class<? extends Saga> sagaClass, Object identifier, Map<String, String> sagaProperties) throws SagaPersistenceException {

            }

            @Override
            public void delete(Class<? extends Saga> sagaClass, Object identifier) throws SagaPersistenceException {

            }
        });
    }

    @Test
    public void load_withGoodSaga_shouldNoThrowException() throws Exception{
        // Given
        UUID identifier = UUID.randomUUID();
        Map<String,String> properties = new HashMap<>();
        properties.put(SagaMapper.X_KASPER_SAGA_CLASS, TestFixture.TestSagaB.class.getName());
        when(baseSagaRepository.doLoad(TestFixture.TestSagaB.class, identifier)).thenReturn(properties);

        // When
        try {
            baseSagaRepository.load(TestFixture.TestSagaB.class, identifier);
        } catch (SagaPersistenceException e) {
            fail();
        }
    }

    @Test
    public void load_withNotSameSaga_shouldThrowException() throws SagaPersistenceException {
        // Given
        UUID identifier = UUID.randomUUID();
        Map<String,String> properties = new HashMap<>();
        properties.put(SagaMapper.X_KASPER_SAGA_CLASS, TestFixture.TestSagaA.class.getName());
        when(baseSagaRepository.doLoad(TestFixture.TestSagaB.class, identifier)).thenReturn(properties);

        // When
        try {
            baseSagaRepository.load(TestFixture.TestSagaB.class, identifier);
        } catch (SagaPersistenceException e) {
            assertNotNull(e);
            assertEquals(String.format("Failed to load a saga instance with '%s' as identifier : mismatch saga type between %s and %s, <saga=%s> <properties=%s>", identifier, TestFixture.TestSagaB.class.getName(), TestFixture.TestSagaA.class.getName(), TestFixture.TestSagaA.class.getName(), properties), e.getMessage());
        }
    }

    @Test
    public void load_withEpmtyProperties_shouldThrowException() throws SagaPersistenceException {
        // Given
        UUID identifier = UUID.randomUUID();
        Map<String,String> properties = new HashMap<>();
        when(baseSagaRepository.doLoad(TestFixture.TestSagaB.class, identifier)).thenReturn(properties);

        // When
        try {
            baseSagaRepository.load(TestFixture.TestSagaB.class, identifier);
        } catch (SagaPersistenceException e) {
            assertNotNull(e);
            assertEquals(String.format("Failed to load a saga instance with '%s' as identifier : saga type is not specified, <properties=%s>", identifier, properties), e.getMessage());
        }
    }
}