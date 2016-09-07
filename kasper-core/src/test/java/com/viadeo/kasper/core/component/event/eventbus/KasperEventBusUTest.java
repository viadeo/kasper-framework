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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.TestDomain;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.GenericEventMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KasperEventBusUTest {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private Meter meter;

    private KasperEventBus eventBus;

    private String expectedMetricName;

    @Before
    public void setUp() throws Exception {
        expectedMetricName = KasperMetrics.name(KasperEventBus.class, "publish");
        when(metricRegistry.meter(expectedMetricName)).thenReturn(meter);
        eventBus = new KasperEventBus(metricRegistry);
    }

    @Test
    public void publish_an_event_should_mark_a_metric() {
        // When
        eventBus.publish(Contexts.empty(), new TestDomain.TestEvent());

        // Then
        verify(metricRegistry).meter(expectedMetricName);
        verify(meter).mark(1);
    }

    @Test
    public void publish_a_message_should_mark_a_metric() {
        // When
        eventBus.publish(GenericEventMessage.asEventMessage(new TestDomain.TestEvent()));

        // Then
        verify(metricRegistry).meter(expectedMetricName);
        verify(meter).mark(1);
    }

    @Test
    public void publish_should_mark_a_metric() throws Exception {
        // When
        eventBus.publish(Contexts.empty(), new TestDomain.TestEvent());

        // Then
        verify(metricRegistry).meter(expectedMetricName);
        verify(meter).mark(1);
    }

    @Test
    public void publishToSuper_messages_should_mark_metrics() {
        // When
        eventBus.publishToSuper(
                GenericEventMessage.asEventMessage(new TestDomain.TestEvent()),
                GenericEventMessage.asEventMessage(new TestDomain.TestEvent())
        );

        // Then
        verify(metricRegistry).meter(expectedMetricName);
        verify(meter).mark(2);
    }
}
