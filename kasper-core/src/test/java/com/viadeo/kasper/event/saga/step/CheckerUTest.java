// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.viadeo.kasper.event.saga.TestFixture;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.collections.Sets;

import java.util.Set;

import static com.viadeo.kasper.event.saga.TestFixture.*;

public class CheckerUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Steps.Checker checker;
    private Class<TestFixture.TestSagaA> sagaClass;

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
    public void check_withSeveralStartSteps_isKO() throws NoSuchMethodException {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId"));
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId"));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId"));

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
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId"));

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
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId"));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId"));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withOneStartStep_withSeveralEndSteps_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId"));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId"));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId"));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withOneStartStep_withOneEndStep_withOtherKindOfSteps_isOK() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId"));
        steps.add(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId"));
        steps.add(new Steps.BasicStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId"));

        // When
        checker.check(sagaClass, steps);

        // Then it's checked successfully!
    }

    @Test
    public void check_withTwoStepsHandlingTheSameEvent_isKO() {
        // Given
        Set<Step> steps = Sets.newSet();
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle0", TestEvent.class), "getId"));
        steps.add(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId"));

        // Then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Should handle an event type per step : <saga=" + TestSagaA.class.getName());

        // When
        checker.check(sagaClass, steps);
    }
}
