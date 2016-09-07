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
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.viadeo.kasper.core.component.event.eventbus.KasperEventBus.Policy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

public class KasperEventBusTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);

    MetricRegistry metricRegistry;

    @Captor
    ArgumentCaptor<GenericEventMessage<Event>> captor;

    @XKasperUnregistered
    private static class TestEvent implements Event {
        private static final long serialVersionUID = 7266657610382378609L;
    }


    public KasperEventBusTest() {
        metricRegistry = new MetricRegistry();
        KasperMetrics.setMetricRegistry(metricRegistry);
    }

    // ------------------------------------------------------------------------

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    // ------------------------------------------------------------------------

    @Test
    public void nominal() throws Exception {
        // Given
        final KasperEventBus eventBus = spy(new KasperEventBus(metricRegistry));
        final TestEvent dummyEvent = new TestEvent();

        // When
        eventBus.publish(Contexts.empty(), dummyEvent);

        // Then
        Mockito.verify(eventBus).publishToSuper(captor.capture());
        final GenericEventMessage<Event> value = captor.getValue();
        assertEquals(dummyEvent, value.getPayload());
        assertTrue(value.getMetaData().containsKey(Context.METANAME));
        assertNotNull(((Context) value.getMetaData().get(Context.METANAME)).getKasperCorrelationId());
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestEventListener extends AutowiredEventListener<TestEvent> {

        private final List<Integer> returns;

        TestEventListener(final List<Integer> returns) {
            this.returns = returns;
        }

        @Override
        public EventResponse handle(Context context, TestEvent event) {
            try {
                LOGGER.info("Begin long running process");
                Thread.sleep(LONG_RUNNING_TIME);
                returns.add(THREAD_RETURNS);
                LOGGER.info("Ended long running process");

                /* Error should not be catched by client */
                throw new RuntimeException("ERROR");

            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ------------------------------------------------------------------------

    private static final Integer LONG_RUNNING_TIME = 1000;

    private static final Integer THREAD_RETURNS = 1;
    private static final Integer EVENT_PUBLISHED = 2;

    @Ignore("unstable test")
    @Test
    public void asynchronous() throws InterruptedException {
        // Given
        final KasperEventBus eventBus = new KasperEventBus(metricRegistry, Policy.ASYNCHRONOUS);
        final List<Integer> returns = Lists.newLinkedList();
        final Event event = new TestEvent();

        // When
        eventBus.subscribe(new TestEventListener(returns));
        LOGGER.info("Publish event");
        eventBus.publish(Contexts.empty(), event);
        LOGGER.info("Event published");
        returns.add(EVENT_PUBLISHED);
        Thread.sleep(3 * LONG_RUNNING_TIME);

        // Then
        assertEquals(2, returns.size());
        assertEquals(EVENT_PUBLISHED, returns.get(0));
        assertEquals(THREAD_RETURNS, returns.get(1));
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestEventErrorListener extends AutowiredEventListener<TestEvent> {
        @Override
        public EventResponse handle(Context context, TestEvent event) {
            throw new RuntimeException("ERROR");
        }
    }


    @Test
    public void listeningSyncError() {
        // Given
        final KasperEventBus syncEventBus = new KasperEventBus(metricRegistry, Policy.SYNCHRONOUS);
        final Event event = new TestEvent();

        // When
        syncEventBus.subscribe(new TestEventErrorListener());
        try {
            syncEventBus.publish(Contexts.empty(), event);
        } catch (final RuntimeException e) {
            // Then ignore
        }
    }

}
