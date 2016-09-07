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
package com.viadeo.kasper.core.component.event.saga;

import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SagaManagerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SagaRepository sagaRepository;
    private SagaManager sagaManager;
    private SagaFactoryProvider sagaFactoryProvider;

    @Before
    public void setUp() throws Exception {
        sagaFactoryProvider = mock(SagaFactoryProvider.class);
        sagaRepository = mock(SagaRepository.class);
        sagaManager = new SagaManager(sagaFactoryProvider, sagaRepository, mock(StepProcessor.class));
    }

    @Test
    public void register_withSaga_returnRelatedExecutor() {
        // Given
        TestFixture.TestSagaA saga = new TestFixture.TestSagaA();
        SagaFactory sagaFactory = mock(SagaFactory.class);
        when(sagaFactoryProvider.getOrCreate(saga)).thenReturn(sagaFactory);

        // When
        SagaExecutor sagaExecutor = sagaManager.register(saga);

        // Then
        assertNotNull(sagaExecutor);
        assertEquals(sagaFactory, sagaExecutor.getSagaFactory());
        assertEquals(sagaRepository, sagaExecutor.getSagaRepository());
        assertEquals(TestFixture.TestSagaA.class, sagaExecutor.getSagaClass());
    }

    @Test
    public void register_withAlreadyRegisteredSaga_isKO() {
        // Given
        TestFixture.TestSagaA saga = new TestFixture.TestSagaA();
        SagaFactory sagaFactory = mock(SagaFactory.class);
        when(sagaFactoryProvider.getOrCreate(saga)).thenReturn(sagaFactory);
        sagaManager.register(saga);

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("The specified saga is already registered");

        // When
        sagaManager.register(saga);
    }
}
