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
import org.mockito.junit.MockitoJUnitRunner;

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