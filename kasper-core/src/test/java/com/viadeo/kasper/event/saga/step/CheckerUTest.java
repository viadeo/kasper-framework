// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.collections.Sets;

import java.util.Set;

public class CheckerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Steps.Checker checker;
    private Class<TestSaga> sagaClass;

    @Before
    public void setUp() throws Exception {
        checker = new Steps.Checker();
        sagaClass = TestSaga.class;
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
        thrown.expectMessage("Should define at less two step methods (start/end) : " + TestSaga.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withSeveralStartSteps_isKO() throws NoSuchMethodException {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle2", TestSaga.TestEvent2.class), "getId"));
        steps.add(new Steps.EndStep(TestSaga.getMethod("handle3", TestSaga.TestEvent3.class), "getId"));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should have one start method : " + TestSaga.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withNoEndSteps_isKO() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should have at less one end method : " + TestSaga.class.getName());

        // When
        checker.check(sagaClass, steps);
    }

    @Test
    public void check_withOneStartStep_withOneEndStep_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));
        steps.add(new Steps.EndStep(TestSaga.getMethod("handle2", TestSaga.TestEvent2.class), "getId"));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withOneStartStep_withSeveralEndSteps_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));
        steps.add(new Steps.EndStep(TestSaga.getMethod("handle2", TestSaga.TestEvent2.class), "getId"));
        steps.add(new Steps.EndStep(TestSaga.getMethod("handle3", TestSaga.TestEvent3.class), "getId"));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withOneStartStep_withOneEndStep_withOtherKindOfSteps_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));
        steps.add(new Steps.EndStep(TestSaga.getMethod("handle2", TestSaga.TestEvent2.class), "getId"));
        steps.add(new Steps.BasicStep(TestSaga.getMethod("handle3", TestSaga.TestEvent3.class), "getId"));
        steps.add(new Steps.ScheduleStep(TestSaga.getMethod("handle4", TestSaga.TestEvent4.class), "getId"));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withTwoStepsHandlingTheSameEvent_isKO() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle0", TestSaga.TestEvent.class), "getId"));
        steps.add(new Steps.StartStep(TestSaga.getMethod("handle", TestSaga.TestEvent.class), "getId"));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should handle an event type per step : " + TestSaga.class.getName());

        // When
        checker.check(sagaClass, steps);
    }
}
