// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.domain.event.Event;
import com.viadeo.kasper.event.EventMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.UUID;

import static com.viadeo.kasper.event.saga.TestFixture.*;
import static org.junit.Assert.*;

public class StepArgumentsUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void init_withoutParametersInMethod_isKO() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("init");

        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal method definition '" + method + "', supported method definitions are : <Event>|<Event,Context>|<EventMessage>");

        // When
        new StepArguments(method);
    }

    @Test
    public void init_withMoreThanTwoParametersInMethod_isKO() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("init", Object.class, Object.class, Object.class);

        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal method definition '" + method + "', supported method definitions are : <Event>|<Event,Context>|<EventMessage>");

        // When
        new StepArguments(method);
    }

    @Test
    public void init_withTwoParametersInMethod_isKO() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("init", Object.class, Object.class);

        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal method definition '" + method + "', definitions with two parameters require the following type : " + Event.class.getName() + " and " + Context.class.getName());

        // When
        new StepArguments(method);
    }

    @Test
    public void init_withAnObjectAsParameterInMethod_isKO() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("init", Object.class);

        // Then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal method definition '" + method + "', definitions with only one parameter can support one of the following types : [" + Event.class.getName() + "|" + EventMessage.class.getName() + "]");

        // When
        new StepArguments(method);
    }

    @Test
    public void init_withEvent_isOK() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle", TestEvent.class);

        // When
        StepArguments stepArguments = new StepArguments(method);

        // Then
        assertNotNull(stepArguments);
        assertEquals(TestEvent.class, stepArguments.getEventClass());
    }

    @Test
    public void init_withEvent_withContext_isOK() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle5", Context.class, TestEvent3.class);

        // When
        StepArguments stepArguments = new StepArguments(method);

        // Then
        assertNotNull(stepArguments);
        assertEquals(TestEvent3.class, stepArguments.getEventClass());
    }

    @Test
    public void init_withEventMessage_isOK() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle6", EventMessage.class);

        // When
        StepArguments stepArguments = new StepArguments(method);

        // Then
        assertNotNull(stepArguments);
        assertEquals(TestEvent3.class, stepArguments.getEventClass());
    }

    @Test
    public void init_withEventMessage_withoutSpecifyGenericType_isOK() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle7", EventMessage.class);

        // When
        StepArguments stepArguments = new StepArguments(method);

        // Then
        assertNotNull(stepArguments);
        assertEquals(Event.class, stepArguments.getEventClass());
    }

    @Test
    public void init_withEventMessage_withWildCard_isOK() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle8", EventMessage.class);

        // When
        StepArguments stepArguments = new StepArguments(method);

        // Then
        assertNotNull(stepArguments);
        assertEquals(Event.class, stepArguments.getEventClass());
    }

    @Test
    public void order_with_method_definition_declaring_event_is_ok() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle", TestEvent.class);
        StepArguments stepArguments = new StepArguments(method);
        TestEvent event = new TestEvent(UUID.randomUUID().toString());
        Context context = Contexts.empty();

        // When
        Object[] orderedObjects = stepArguments.order(context, event);

        // Then
        assertNotNull(orderedObjects);
        assertTrue(orderedObjects.length == 1);
        assertEquals(event, orderedObjects[0]);
    }

    @Test
    public void order_with_method_definition_declaring_event_and_context_is_ok() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle5", TestEvent3.class, Context.class);
        StepArguments stepArguments = new StepArguments(method);
        TestEvent3 event = new TestEvent3(UUID.randomUUID().toString());
        Context context = Contexts.empty();

        // When
        Object[] orderedObjects = stepArguments.order(context, event);

        // Then
        assertNotNull(orderedObjects);
        assertTrue(orderedObjects.length == 2);
        assertEquals(event, orderedObjects[0]);
        assertEquals(context, orderedObjects[1]);
    }

    @Test
    public void order_with_method_definition_declaring_event_message_is_ok() throws NoSuchMethodException {
        // Given
        Method method = TestSagaA.class.getMethod("handle6", EventMessage.class);
        StepArguments stepArguments = new StepArguments(method);
        TestEvent3 event = new TestEvent3(UUID.randomUUID().toString());
        Context context = Contexts.empty();

        // When
        Object[] orderedObjects = stepArguments.order(context, event);

        // Then
        assertNotNull(orderedObjects);
        assertTrue(orderedObjects.length == 1);
        assertTrue(orderedObjects[0] instanceof EventMessage);

        EventMessage eventMessage = (EventMessage) orderedObjects[0];
        assertEquals(event, eventMessage.getEvent());
        assertEquals(context, eventMessage.getContext());
    }
}
