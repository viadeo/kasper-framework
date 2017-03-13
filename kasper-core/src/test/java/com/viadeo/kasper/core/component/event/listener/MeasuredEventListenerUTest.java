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

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.MeasuredHandler;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeasuredEventListenerUTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Test
    public void propagate_a_caught_runtime_exception() {
        // Given
        MetricRegistry metricRegistry = spy(new MetricRegistry());

        EventListener<Event> handler = (EventListener) spy(new TestEventListener());
        doThrow(new RuntimeException("bazinga!")).when(handler).handle(any(EventMessage.class));

        MeasuredHandler measuredHandler = new MeasuredEventListener(metricRegistry, handler);

        // Then
        exception.expect(RuntimeException.class);
        exception.expectMessage("bazinga!");

        // When
        measuredHandler.handle(new EventMessage(
                Optional.<KasperID>absent(),
                DateTime.now(),
                Contexts.empty(),
                mock(Event.class)
        ));
    }

    static class StringRegexMatcher extends BaseMatcher<String> {

        private final String regex;

        public StringRegexMatcher(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean matches(Object item) {
            return String.valueOf(item).matches(regex);
        }

        @Override
        public void describeTo(Description description) { }
    }

    @XKasperUnregistered
    @XKasperDomain(prefix = "test", label = "test")
    public static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperEvent(action = "test")
    private static class TestEvent implements Event {}

    @XKasperUnregistered
    @XKasperEventListener(domain = TestDomain.class)
    private static class TestEventListener extends AutowiredEventListener<TestEvent> {

        @Override
        public EventResponse handle(Context context, TestEvent event) {
            return EventResponse.success();
        }
    }
}
