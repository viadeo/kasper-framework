// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.MyCustomDomainBox;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;

public class KasperMetricsITest {

    private static void buildPlatformWith(final MetricRegistry metricRegistry) {
        new Platform.Builder(new KasperPlatformConfiguration())
                .withMetricRegistry(metricRegistry)
                .build();
    }

    // ------------------------------------------------------------------------

    @Test
    public void getMetricRegistry_withBuiltPlatform_shouldBeOk() {
        // Given
        final MetricRegistry metricRegistry = new MetricRegistry();
        buildPlatformWith(metricRegistry);

        // When
        final MetricRegistry actualMetricRegistry = KasperMetrics.getMetricRegistry();

        //Then
        assertEquals(metricRegistry, actualMetricRegistry);
    }

    @Test
    public void name_fromClass_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(ExecutorService.class, "bip");

        // Then
        assertEquals((ExecutorService.class.getName() + ".bip").toLowerCase(), name);
    }

    @Test
    public void name_fromCommandHandler_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomCommandHandler.class, "bip");

        // Then
        assertEquals("mycustomdomain.commandhandler.mycustomcommandhandler.bip", name);
    }

    @Test
    public void name_fromQueryHandler_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomQueryHandler.class, "bip");

        // Then
        assertEquals("mycustomdomain.queryhandler.mycustomqueryhandler.bip", name);
    }

    @Test
    public void name_fromMyCustomEventListener_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomEventListener.class, "bip");

        // Then
        assertEquals("mycustomdomain.eventlistener.mycustomeventlistener.bip", name);
    }

    @Test
    public void name_fromMyCustomRepository_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomRepository.class, "bip");

        // Then
        assertEquals("mycustomdomain.repository.mycustomrepository.bip", name);
    }

    @Test
    public void name_fromMyCustomCommand_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomCommand.class, "bip");

        // Then
        assertEquals((MyCustomDomainBox.MyCustomCommand.class.getName() + ".bip").toLowerCase(), name);
    }

    @Test
    public void name_fromMyCustomQuery_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomQuery.class, "bip");

        // Then
        assertEquals((MyCustomDomainBox.MyCustomQuery.class.getName() + ".bip").toLowerCase(), name);
    }

    @Test
    public void name_fromMyCustomQueryResult_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomQueryResult.class, "bip");

        // Then
        assertEquals((MyCustomDomainBox.MyCustomQueryResult.class.getName() + ".bip").toLowerCase(), name);
    }

}
