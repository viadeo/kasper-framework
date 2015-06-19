package com.viadeo.kasper.event.saga.step;

import com.google.common.collect.Sets;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.TestFixture;
import org.junit.Test;

import java.util.Set;

import static com.viadeo.kasper.event.saga.TestFixture.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class StepProcessorUTest {

    @Test
    public void process_resolveThenCheck() {
        // Given
        Set<Step> givenSteps = Sets.newHashSet();
        StepChecker checker = mock(StepChecker.class);
        StepResolver resolver = mock(StepResolver.class);
        when(resolver.resolve(Saga.class)).thenReturn(givenSteps);
        StepProcessor processor = new StepProcessor(checker, resolver);

        // When
        Set<Step> steps = processor.process(Saga.class);

        // Then
        assertNotNull(steps);
        verify(resolver).resolve(Saga.class);
        verify(checker).check(Saga.class, givenSteps);
    }

    @Test
    public void process_withTestSaga_isOK() {
        // Given
        StepProcessor processor = new StepProcessor();

        // When
        Set<Step> steps = processor.process(TestFixture.TestSagaA.class);

        // Then
        assertNotNull(steps);
        assertTrue(steps.size() == 4);
        assertTrue(steps.contains(new Steps.StartStep(getMethod(TestSagaA.class, "handle", TestEvent.class), "getId")));
        assertTrue(steps.contains(new Steps.EndStep(getMethod(TestSagaA.class, "handle2", TestEvent2.class), "getId")));
        assertTrue(steps.contains(new Steps.BasicStep(getMethod(TestSagaA.class, "handle3", TestEvent3.class), "getId")));
        assertTrue(steps.contains(new Steps.ScheduleStep(getMethod(TestSagaA.class, "handle4", TestEvent4.class), "getId")));
    }
}
