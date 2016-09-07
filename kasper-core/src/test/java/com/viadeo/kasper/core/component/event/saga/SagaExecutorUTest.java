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

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.event.saga.exception.SagaExecutionException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

public class SagaExecutorUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SagaFactory factory;
    private SagaRepository repository;
    private SagaExecutor executor;
    private Step startStep;
    private Step basicStep;

    @Before
    public void setUp() throws Exception {
        SagaIdReconciler idReconciler = SagaIdReconciler.NONE;
        factory = mock(SagaFactory.class);
        repository = mock(SagaRepository.class);
        startStep = spy(new Steps.StartStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "handle", TestFixture.TestEvent.class), "getId", idReconciler));
        basicStep = spy(new Steps.BasicStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "handle2", TestFixture.TestEvent2.class), "getId", idReconciler));
        executor = new SagaExecutor<>(
                TestFixture.TestSagaA.class,
                Sets.<Step>newHashSet(startStep, basicStep),
                factory,
                repository
        );
    }

    @Test
    public void execute_withUnknownEvent_isOK() {
        // Then
        expectedException.expect(SagaExecutionException.class);
        expectedException.expectMessage("No step defined in the optionalSaga 'TestSagaA' to the specified event");

        // When
        executor.execute(Contexts.empty(), new Event() { });
    }

    @Test
    public void execute_withAnEventAssociatedToAStartStep_createNewSaga() throws SagaPersistenceException {
        // Given
        TestFixture.TestEvent event = new TestFixture.TestEvent("2015");
        TestFixture.TestSagaA saga = new TestFixture.TestSagaA();
        Context context = Contexts.empty();
        when(factory.create("2015", TestFixture.TestSagaA.class)).thenReturn(saga);
        when(repository.load(TestFixture.TestSagaA.class, "2015")).thenReturn(Optional.<TestFixture.TestSagaA>absent());

        // When
        executor.execute(context, event);

        // Then
        verify(factory).create("2015", TestFixture.TestSagaA.class);
        verify(startStep).invoke(saga, context, event);
    }

    @Test
    public void execute_withAnEventAssociatedToAStepExceptAStart_loadSaga() throws SagaPersistenceException {
        // Given
        TestFixture.TestEvent2 event = new TestFixture.TestEvent2("2015");
        TestFixture.TestSagaA saga = new TestFixture.TestSagaA();
        Context context = Contexts.empty();
        when(repository.load(TestFixture.TestSagaA.class, "2015")).thenReturn(Optional.<TestFixture.TestSagaA>of(saga));

        // When
        executor.execute(context, event);

        // Then
        verify(factory, never()).create("2015", TestFixture.TestSagaA.class);
        verify(repository).load(TestFixture.TestSagaA.class, "2015");
        verify(basicStep).invoke(saga, context, event);
    }
}
