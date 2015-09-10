// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.platform.builder.DefaultPlatform;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;

public class KasperMetricsITest {

    private static void buildPlatformWith(final MetricRegistry metricRegistry) {
        DefaultPlatform.builder(new KasperPlatformConfiguration())
                .withMetricRegistry(metricRegistry)
                .addDomainBundle(MyCustomDomainBox.getBundle())
                .build();

        // clear caches in order to ensure test integrity
        KasperMetrics.clearCache();
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
    public void name_fromEventListenerClass_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(EventListener.class, "bip");

        // Then
        assertEquals((EventListener.class.getName() + ".bip").toLowerCase(), name);
    }

    @Test
    public void name_fromMyCustomCommandHandler_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomCommandHandler.class, "bip");

        // Then
        assertEquals("mycustomdomain.commandhandler.mycustomcommandhandler.bip", name);
    }

    @Test
    public void name_fromMyCustomQueryHandler_shouldBeOk() {
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
        assertEquals("mycustomdomain.command.mycustomcommand.bip", name);
    }

    @Test
    public void name_fromMyCustomCommand_withDomainTypeAsStyle_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(
            MetricNameStyle.DOMAIN_TYPE,
            MyCustomDomainBox.MyCustomCommand.class,
            "bip"
        );

        // Then
        assertEquals("mycustomdomain.command.bip", name);
    }

    @Test
    public void name_fromMyCustomCommand_withDomainTypeComponentAsStyle_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(
            MetricNameStyle.DOMAIN_TYPE_COMPONENT,
            MyCustomDomainBox.MyCustomCommand.class,
            "bip"
        );

        // Then
        assertEquals("mycustomdomain.command.mycustomcommand.bip", name);
    }

    @Test
    public void name_fromMyCustomCommand_withNoneAsStyle_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MetricNameStyle.NONE, MyCustomDomainBox.MyCustomCommand.class, "bip");

        // Then
        assertEquals("com.viadeo.kasper.platform.bundle.sample.mycustomdomainbox$mycustomcommand.bip", name);
    }

    @Test
    public void name_fromMyCustomQuery_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomQuery.class, "bip");

        // Then
        assertEquals("mycustomdomain.query.mycustomquery.bip", name);
    }

    @Test
    public void name_fromMyCustomQueryResult_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomQueryResult.class, "bip");

        // Then
        assertEquals("mycustomdomain.queryresult.mycustomqueryresult.bip", name);
    }

    @Test
    public void name_fromMyCustomEvent_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomEvent.class, "bip");

        // Then
        assertEquals("unknown.event.mycustomevent.bip", name);
    }

    @Test
    public void name_fromMyCustomDomainEvent_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        final String name = KasperMetrics.name(MyCustomDomainBox.MyCustomDomainEvent.class, "bip");

        // Then
        assertEquals("mycustomdomain.event.mycustomdomainevent.bip", name);
    }

    @Test(expected = KasperException.class)
    public void name_MyCustomMalformedDomainEvent_shouldThrownException() {
        // Given
        buildPlatformWith(new MetricRegistry());

        // When
        KasperMetrics.name(MyCustomDomainBox.MyCustomMalformedDomainEvent.class, "bip");

        // Then throws an exception
    }

    @Test
    public void name_onClientPerType_fromMyCustomQuery_withSpecifiedApplicationId_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        final Context context = Contexts.builder().withClientId("foobar").build();

        // When
        final String name = KasperMetrics.name(MetricNameStyle.CLIENT_TYPE, context, MyCustomDomainBox.MyCustomQuery.class, "bip");

        // Then
        assertEquals("client.foobar.query.bip", name);
    }

    @Test
    public void name_onClientPerType_fromMyCustomQuery_withUnspecifiedApplicationId_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        final Context context = Contexts.empty();

        // When
        final String name = KasperMetrics.name(MetricNameStyle.CLIENT_TYPE, context, MyCustomDomainBox.MyCustomQuery.class, "bip");

        // Then
        assertEquals("client.unknown.query.bip", name);
    }

    @Test
    public void name_onClientPerType_fromMyCustomCommand_withSpecifiedApplicationId_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        final Context context = Contexts.builder().withClientId("foobar").build();

        // When
        final String name = KasperMetrics.name(MetricNameStyle.CLIENT_TYPE, context, MyCustomDomainBox.MyCustomCommand.class, "bip");

        // Then
        assertEquals("client.foobar.command.bip", name);
    }

    @Test
    public void name_onClientPerType_fromMyCustomCommand_withUnspecifiedApplicationId_shouldBeOk() {
        // Given
        buildPlatformWith(new MetricRegistry());

        final Context context = Contexts.empty();

        // When
        final String name = KasperMetrics.name(MetricNameStyle.CLIENT_TYPE, context, MyCustomDomainBox.MyCustomCommand.class, "bip");

        // Then
        assertEquals("client.unknown.command.bip", name);
    }

}
