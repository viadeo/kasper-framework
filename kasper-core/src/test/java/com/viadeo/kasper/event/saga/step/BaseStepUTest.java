// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.Event;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class BaseStepUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void init_withoutParametersInMethod_isKO() throws NoSuchMethodException {
        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Should specify only one and unique parameter referencing an event : ");

        // When
        new BaseStep(TestSaga.class.getMethod("init"), "") {};
    }

    @Test
    public void init_withSeveralParametersInMethod_isKO() throws NoSuchMethodException {
        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Should specify only one and unique parameter referencing an event : ");

        // When
        new BaseStep(TestSaga.class.getMethod("init", Object.class, Object.class), "") {};
    }

    @Test
    public void init_withAnObjectAsParameterInMethod_isKO() throws NoSuchMethodException {
        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Should specify an instance of event as parameter :");

        // When
        new BaseStep(TestSaga.class.getMethod("init", Object.class), "") {};
    }

    @Test
    public void init_withUnknownGetterName_isKO() throws NoSuchMethodException {
        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The specified getter name 'fakeGetter' is undefined in the event");

        // When
        new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "fakeGetter") {};
    }

    @Test
    public void init_isOK() throws NoSuchMethodException {
        new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId") {};
    }


    @Test
    public void name_returnTheMethodName() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");

        // When
        String name = step.name();

        // Then
        assertNotNull(name);
        assertEquals("handle", name);
    }

    @Test
    public void getSupportedEvent_returnTheTypeParameterOfTheSpecifiedMethod() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");

        // When
        Class<? extends Event> supportedEvent = step.getSupportedEvent();

        // Then
        assertNotNull(supportedEvent);
        assertEquals(TestSaga.TestEvent.class, supportedEvent);
    }

    @Test
    public void equals_withDifferentName_isKO() throws NoSuchMethodException {
        // Given
        Step step1 = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");
        Step step2 = new BaseStep(TestSaga.class.getMethod("handle2", TestSaga.TestEvent2.class), "getId");

        // When
        boolean equality = step1.equals(step2);

        // Then
        assertFalse(equality);
    }

    @Test
    public void equals_withDifferentSupportedEvent_isKO() throws NoSuchMethodException {
        // Given
        Step step1 = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");
        Step step2 = new BaseStep(TestSaga.class.getMethod("handle2", TestSaga.TestEvent2.class), "getId");

        // When
        boolean equality = step1.equals(step2);

        // Then
        assertFalse(equality);
    }

    @Test
    public void equals_withSameName_withSameSupportedEvent_isOK() throws NoSuchMethodException {
        // Given
        Step step1 = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");
        Step step2 = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");

        // When
        boolean equality = step1.equals(step2);

        // Then
        assertTrue(equality);
    }

    @Test
    public void getSagaIdentifierFrom_returnTheValue() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getId");
        Event event = new TestSaga.TestEvent("42");

        // When
        Optional<String> identifier = step.getSagaIdentifierFrom(event);

        // Then
        assertNotNull(identifier);
        assertTrue(identifier.isPresent());
        assertEquals("42", identifier.get());
    }

    @Test
    public void getSagaIdentifierFrom_withUnexpectedError_returnAbsentValue() throws NoSuchMethodException {
        // Given
        Step step = new BaseStep(TestSaga.class.getMethod("handle", TestSaga.TestEvent.class), "getIdThrowsException");
        Event event = new TestSaga.TestEvent("42");

        // When
        Optional<String> identifier = step.getSagaIdentifierFrom(event);

        // Then
        assertNotNull(identifier);
        assertFalse(identifier.isPresent());
    }
}
