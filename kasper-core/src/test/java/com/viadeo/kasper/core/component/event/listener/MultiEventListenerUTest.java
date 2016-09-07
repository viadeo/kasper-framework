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
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

public class MultiEventListenerUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void upon_initialization_we_discover_all_annotated_method_is_ok() {
        MultiEventListenerTest eventListener = new MultiEventListenerTest();
        assertEquals(2, eventListener.getHandlerByEventClasses().size());
    }

    @Test
    public void the_event_handling_is_ok() {
        // Given
        MultiEventListenerTest eventListener = spy(new MultiEventListenerTest());
        Context context = Contexts.empty();
        EventA eventA = new EventA(UUID.randomUUID());

        // When
        EventResponse eventResponse = eventListener.handle(new EventMessage<Event>(Optional.<KasperID>absent(), DateTime.now(), context, eventA));

        // Then
        assertNotNull(eventResponse);
        assertTrue(eventListener.handledEvents.size() == 1);
        assertTrue(eventListener.handledEvents.contains(eventA));
    }

    @Test
    public void get_parameters_of_a_method_declaring_only_an_event_is_ok() throws NoSuchMethodException {
        // Given
        MultiEventListener.Handler handler = new MultiEventListener.Handler(
                MultiEventListenerTest.class.getDeclaredMethod("doHandle", EventB.class),
                new MultiEventListenerTest()
        );
        Context context = Contexts.empty();
        EventB eventB = new EventB(UUID.randomUUID());

        // When
        Object[] parameters = handler.parameters(context, eventB);

        // Then
        assertNotNull(parameters);
        assertTrue(parameters.length == 1);
        assertEquals(eventB, parameters[0]);
    }

    @Test
    public void get_parameters_of_a_method_declaring_an_event_and_a_context_is_ok() throws NoSuchMethodException {
        // Given
        MultiEventListener.Handler handler = new MultiEventListener.Handler(
                MultiEventListenerTest.class.getDeclaredMethod("doHandle", EventA.class, Context.class),
                new MultiEventListenerTest()
        );
        Context context = Contexts.empty();
        EventA eventA = new EventA(UUID.randomUUID());

        // When
        Object[] parameters = handler.parameters(context, eventA);

        // Then
        assertNotNull(parameters);
        assertTrue(parameters.length == 2);
        assertEquals(eventA, parameters[0]);
        assertEquals(context, parameters[1]);
    }

    @Test
    public void get_parameters_of_a_method_declaring_an_object_that_is_not_expected_then_throws_an_exception() throws NoSuchMethodException {
        // Given
        MultiEventListener.Handler handler = new MultiEventListener.Handler(
                MultiEventListenerTest.class.getDeclaredMethod("doHandle", String.class),
                new MultiEventListenerTest()
        );
        Context context = Contexts.empty();
        EventA eventA = new EventA(UUID.randomUUID());

        // Then
        expectedException.expect(KasperException.class);
        expectedException.expectMessage("Error during handling event by the method 'doHandle'");

        // When
        handler.parameters(context, eventA);
    }

    // ------------------------------------------------------------------------

    public static class MultiEventListenerTest extends MultiEventListener {
        public final Set<Event> handledEvents = Sets.newHashSet();

        @Handle
        public EventResponse doHandle(EventA event, Context context) {
            System.err.println("EventA !");
            handledEvents.add(event);
            return EventResponse.success();
        }

        @Handle
        public EventResponse doHandle(EventB event) {
            System.err.println("EventB !");
            handledEvents.add(event);
            return EventResponse.success();
        }

        @Handle
        public EventResponse doHandle(String value) {
            System.err.println("parameters without events");
            return EventResponse.success();
        }
    }

    public static class EventA extends TestFixture.AbstractEvent {
        public EventA(UUID id) {
            super(id);
        }
    }

    public static class EventB extends TestFixture.AbstractEvent {
        public EventB(UUID id) {
            super(id);
        }
    }
}
