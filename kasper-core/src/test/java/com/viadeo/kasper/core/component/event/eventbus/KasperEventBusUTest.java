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
