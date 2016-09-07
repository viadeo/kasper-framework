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
package com.viadeo.kasper.core.component.event;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Objects;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EventListenerUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AutowiredEventListener<TestEvent> listener;

    @Before
    public void setUp() throws Exception {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        listener = spy(new TestEventListener());
    }

    @Test
    public void handle_withSuccessAsResponse_isOk() {
        // Given
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // When
        listener.handle(eventMessage);

        // Then
        verify(listener).handle(any(Context.class), any(TestEvent.class));
    }

    @Test
    public void handle_withIncompatibleEvent_isOk() {
        // Given
        GenericEventMessage<TestEvent2> eventMessage = new GenericEventMessage<>(new TestEvent2(), Contexts.empty().asMetaDataMap());

        // When
        listener.handle(eventMessage);

        // Then
        verify(listener, never()).handle(any(Context.class), any(TestEvent.class));
    }

    @Test
    public void handle_withErrorAsResponse_throwException() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "bazinga!!")));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.core.component.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{ERROR, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [bazinga!!]}}>"));

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withFailureAsResponse_throwException() {
        // Given
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "bazinga!!")));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.core.component.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{FAILURE, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [bazinga!!]}}>"));

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withFailureAsResponse_containingExceptionInReason_wrapException() {
        // Given
        RuntimeException exception = new RuntimeException("Fake exception");
        when(listener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, exception)));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.core.component.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{FAILURE, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [Fake exception]}}>"));

        // When
        listener.handle(eventMessage);
    }

    @Test
    public void handle_withUnexpectedException_propagatesException() {
        // Given
        doThrow(new RuntimeException("bazinga!")).when(listener).handle(any(Context.class), any(TestEvent.class));
        GenericEventMessage<TestEvent> eventMessage = new GenericEventMessage<>(new TestEvent(), Contexts.empty().asMetaDataMap());

        // Then
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(equalsTo("Failed to handle event class com.viadeo.kasper.core.component.event.EventListenerUTest$TestEvent, <event=TestEvent{}> <response=EventResponse{FAILURE, KasperReason{_UUID_, INTERNAL_COMPONENT_ERROR, [bazinga!]}}>"));

        // When
        listener.handle(eventMessage);
    }

    private static Matcher<String> equalsTo(final String expected) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return o.toString().replaceAll("\\w{8}(-\\w{4}){3}-\\w{12}?", "_UUID_").equals(expected);
            }

            @Override
            public void describeTo(Description description) { }
        };
    }

    private static class TestEventListener extends AutowiredEventListener<TestEvent> {
        @Override
        public EventResponse handle(Context context, TestEvent event) {
            return EventResponse.success();
        }
    }

    private static class TestEvent implements Event {
        @Override
        public String toString() {
            return Objects.toStringHelper(this).toString();
        }
    }

    private static class TestEvent2 implements Event {
        @Override
        public String toString() {
            return Objects.toStringHelper(this).toString();
        }
    }
}
