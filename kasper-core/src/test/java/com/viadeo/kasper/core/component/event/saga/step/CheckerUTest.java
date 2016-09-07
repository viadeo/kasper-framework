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
package com.viadeo.kasper.core.component.event.saga.step;

import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.step.facet.SchedulingStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.collections.Sets;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.viadeo.kasper.core.component.event.saga.TestFixture.*;
import static org.mockito.Mockito.mock;

public class CheckerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Steps.Checker checker;
    private Class<? extends Saga> sagaClass;

    @Before
    public void setUp() throws Exception {
        checker = new Steps.Checker();
        sagaClass = TestFixture.TestSagaA.class;
    }

    @Test
    public void check_withNullAsSteps_isKO() {
        // Then
        thrown.expect(NullPointerException.class);

        // When
        checker.check(sagaClass, null);
    }

    @Test
    public void check_withNoSteps_isKO() {
        // Given
        Set<Step> steps = Sets.newSet();

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should define at less two step methods (start/end) : " + TestSagaA.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withOneDecorateStartStep_isOK() throws NoSuchMethodException {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(
                new SchedulingStep(
                        new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)),
                        new SchedulingStep.ScheduleOperation(mock(Scheduler.class), TestSagaA.class, "init", 4L, TimeUnit.MILLISECONDS, false)
                )
        );

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withSeveralStartSteps_isKO() throws NoSuchMethodException {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId", mock(SagaIdReconciler.class)));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should have one start method : " + TestSagaA.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withNoEndSteps_isKO() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should have at less one end method : " + TestFixture.TestSagaA.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withOneStartStep_withOneEndStep_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId", mock(SagaIdReconciler.class)));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withOneStartStep_withSeveralEndSteps_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId", mock(SagaIdReconciler.class)));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withOneStartStep_withOneEndStep_withOtherKindOfSteps_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.BasicStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId", mock(SagaIdReconciler.class)));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withTwoStepsHandlingTheSameEvent_isKO() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle0", TestEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId", mock(SagaIdReconciler.class)));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should handle an event type per step : <saga=" + TestSagaA.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withScheduleByEventStep_withSchedulableSagaMethodEvent_isOk() {
        // Given
        sagaClass = TestFixture.TestSagaB.class;
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaB.class, "start", StartEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaB.class, "end", EndEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new SchedulingStep(
                new Steps.BasicStep(getMethod(TestSagaB.class, "scheduledByEventStep", StepEvent4.class), "getId", mock(SagaIdReconciler.class)),
                new SchedulingStep.ScheduleByEventOperation(mock(Scheduler.class), TestSagaA.class, "init", false)
        ));

        // Then
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withScheduleByEventStep_withoutSchedulableSagaMethodEvent_isKo() {
        // Given
        sagaClass = TestFixture.TestSagaB.class;
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaB.class, "start", StartEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new Steps.EndStep(getMethod(TestSagaB.class, "end", EndEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(new SchedulingStep(
                new Steps.BasicStep(getMethod(TestSagaC.class, "scheduledByEventStep", StepEvent1.class), "getId", mock(SagaIdReconciler.class)),
                new SchedulingStep.ScheduleByEventOperation(mock(Scheduler.class), TestSagaA.class, "init", false)
        ));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("The event should be assignment-compatible with");

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withScheduleStep_onEndStep_isKo() {
        // Given
        sagaClass = TestFixture.TestSagaC.class;
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaC.class, "start", StartEvent.class), "getId", mock(SagaIdReconciler.class)));
        steps.add(
                new SchedulingStep(
                        new Steps.EndStep(getMethod(TestSagaC.class, "endWithCancelSchedule", StepEvent3.class), "getId", mock(SagaIdReconciler.class)),
                        new SchedulingStep.CancelOperation(mock(Scheduler.class), TestSagaC.class, "invokedMethod")
                )
        );

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(String.format("Should not use scheduling step on an end step : %s" , TestSagaC.class.getName()));

        // When
        checker.check(sagaClass, steps);
    }
}
