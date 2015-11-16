// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class BaseStepUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SagaIdReconciler idReconciler;

    @Before
    public void setUp() throws Exception {
        idReconciler = SagaIdReconciler.NONE;
    }

    @Test
    public void init_withUnknownGetterName_isKO() throws NoSuchMethodException {
        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The specified getter name 'fakeGetter' is undefined in the event");

        // When
        new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "fakeGetter", idReconciler) {};
    }

    @Test
    public void init_isOK() throws NoSuchMethodException {
        new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler) {};
    }


    @Test
    public void name_returnTheMethodName() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);

        // When
        String name = step.name();

        // Then
        assertNotNull(name);
        assertEquals("handle", name);
    }

    @Test
    public void getSupportedEvent_returnTheTypeParameterOfTheSpecifiedMethod() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);

        // When
        Class supportedEvent = step.getSupportedEvent().getEventClass();

        // Then
        assertNotNull(supportedEvent);
        assertEquals(TestFixture.TestEvent.class, supportedEvent);
    }

    @Test
    public void equals_withDifferentName_isKO() throws NoSuchMethodException {
        // Given
        Step step1 = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);
        Step step2 = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle2", TestFixture.TestEvent2.class), "step", "getId", idReconciler);

        // When
        boolean equality = step1.equals(step2);

        // Then
        assertFalse(equality);
    }

    @Test
    public void equals_withDifferentSupportedEvent_isKO() throws NoSuchMethodException {
        // Given
        Step step1 = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);
        Step step2 = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle2", TestFixture.TestEvent2.class), "step", "getId", idReconciler);

        // When
        boolean equality = step1.equals(step2);

        // Then
        assertFalse(equality);
    }

    @Test
    public void equals_withSameName_withSameSupportedEvent_isOK() throws NoSuchMethodException {
        // Given
        Step step1 = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);
        Step step2 = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);

        // When
        boolean equality = step1.equals(step2);

        // Then
        assertTrue(equality);
    }

    @Test
    public void getSagaIdentifierFrom_returnTheValue() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getId", idReconciler);
        Event event = new TestFixture.TestEvent("42");

        // When
        Optional<Object> identifier = step.getSagaIdentifierFrom(event);

        // Then
        assertNotNull(identifier);
        assertTrue(identifier.isPresent());
        assertEquals("42", identifier.get());
    }

    @Test
    public void getSagaIdentifierFrom_withUnexpectedError_returnAbsentValue() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestFixture.TestSagaA.class.getMethod("handle", TestFixture.TestEvent.class), "step", "getIdThrowsException", idReconciler);
        Event event = new TestFixture.TestEvent("42");

        // When
        Optional<Object> identifier = step.getSagaIdentifierFrom(event);

        // Then
        assertNotNull(identifier);
        assertFalse(identifier.isPresent());
    }
}
