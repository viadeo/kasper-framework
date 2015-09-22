// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryHandlerInterceptor;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.metrics.MetricNames;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MetricsPublicationITest {

    private Platform platform;
    private MetricRegistry metricRegistry;
    private MetricNames commandGatewayMetricNames;
    private MetricNames queryGatewayMetricNames;

    @Before
    public void init() {
        metricRegistry = spy(new MetricRegistry());

        platform = Platforms.newDefaultBuilder(new KasperPlatformConfiguration(metricRegistry))
                .addDomainBundle(MyCustomDomainBox.getBundle())
                .build();

        commandGatewayMetricNames = MetricNames.of(CommandGateway.class);
        queryGatewayMetricNames = MetricNames.of(QueryGateway.class);

        // clear caches in order to ensure test integrity
        KasperMetrics.clearCache();
    }

    @Test
    public void checkMetricsPublication_onOverall_fromSuccessfulCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Meter globalRequestsNameMeter = registerSpyMeter(commandGatewayMetricNames.requests);
        final Timer globalRequestsNameTimer = registerSpyTimer(commandGatewayMetricNames.requestsTime);

        reset(metricRegistry);

        // When
        platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(), Contexts.empty());

        // Then
        verifyTimerInteraction(commandGatewayMetricNames.requestsTime, globalRequestsNameTimer);
        verifyMeterInteraction(commandGatewayMetricNames.requests, globalRequestsNameMeter);
    }

    @Test
    public void checkMetricsPublication_onOverall_fromFailedCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Timer globalRequestsNameTimer = registerSpyTimer(commandGatewayMetricNames.requestsTime);
        final Meter globalRequestsNameMeter = registerSpyMeter(commandGatewayMetricNames.requests);
        final Meter globalErrorsNameMeter = registerSpyMeter(commandGatewayMetricNames.errors);

        reset(metricRegistry);

        // When
        try {
            platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(false), Contexts.empty());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction(commandGatewayMetricNames.requestsTime, globalRequestsNameTimer);
        verifyMeterInteraction(commandGatewayMetricNames.requests, globalRequestsNameMeter);
        verifyMeterInteraction(commandGatewayMetricNames.errors, globalErrorsNameMeter);
    }

    @Test
    public void checkMetricsPublication_onDomainPerTypeAndComponent_fromSuccessfulCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Meter requestsNameMeter = registerSpyMeter("mycustomdomain.command.mycustomcommand.requests");
        final Timer requestsNameTimer = registerSpyTimer("mycustomdomain.command.mycustomcommand.requests-handle-time");

        reset(metricRegistry);

        // When
        platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(), Contexts.empty());

        // Then
        verifyTimerInteraction("mycustomdomain.command.mycustomcommand.requests-handle-time", requestsNameTimer);
        verifyMeterInteraction("mycustomdomain.command.mycustomcommand.requests", requestsNameMeter);

    }

    @Test
    public void checkMetricsPublication_onDomainPerTypeAndComponent_fromFailedCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Timer requestsNameTimer = registerSpyTimer("mycustomdomain.command.mycustomcommand.requests-handle-time");
        final Meter requestsNameMeter = registerSpyMeter("mycustomdomain.command.mycustomcommand.requests");
        final Meter errorsNameMeter = registerSpyMeter("mycustomdomain.command.mycustomcommand.errors");

        reset(metricRegistry);

        // When
        try {
            platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(false), Contexts.empty());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction("mycustomdomain.command.mycustomcommand.requests-handle-time", requestsNameTimer);
        verifyMeterInteraction("mycustomdomain.command.mycustomcommand.requests", requestsNameMeter);
        verifyMeterInteraction("mycustomdomain.command.mycustomcommand.errors", errorsNameMeter);
    }

    @Test
    public void checkMetricsPublication_onDomainPerType_fromSuccessfulCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Meter domainRequestsNameMeter = registerSpyMeter("mycustomdomain.command.requests");
        final Timer domainRequestsNameTimer = registerSpyTimer("mycustomdomain.command.requests-handle-time");

        reset(metricRegistry);

        // When
        platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(), Contexts.empty());

        // Then
        verifyTimerInteraction("mycustomdomain.command.requests-handle-time", domainRequestsNameTimer);
        verifyMeterInteraction("mycustomdomain.command.requests", domainRequestsNameMeter);
    }

    @Test
    public void checkMetricsPublication_onDomainPerType_fromFailedCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Timer domainRequestsNameTimer = registerSpyTimer("mycustomdomain.command.requests-handle-time");
        final Meter domainRequestsNameMeter = registerSpyMeter("mycustomdomain.command.requests");
        final Meter domainErrorsNameTimer = registerSpyMeter("mycustomdomain.command.errors");

        reset(metricRegistry);

        // When
        try {
            platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(false), Contexts.empty());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction("mycustomdomain.command.requests-handle-time", domainRequestsNameTimer);
        verifyMeterInteraction("mycustomdomain.command.requests", domainRequestsNameMeter);
        verifyMeterInteraction("mycustomdomain.command.errors", domainErrorsNameTimer);
    }

    @Test
    public void checkMetricsPublication_onClientPerType_fromSuccessfulCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Meter requestMeter = registerSpyMeter("client.myconsumer.command.requests");

        reset(metricRegistry);

        final Context context = Contexts.builder().withClientId("myconsumer").build();

        // When
        platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(), context);

        // Then
        verifyMeterInteraction("client.myconsumer.command.requests", requestMeter);
    }

    @Test
    public void checkMetricsPublication_onClientPerType_fromFailedCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Meter requestsMeter = registerSpyMeter("client.myconsumer.command.requests");
        final Meter errorsMeter = registerSpyMeter("client.myconsumer.command.errors");

        reset(metricRegistry);

        final Context context = Contexts.builder().withClientId("myconsumer").build();

        // When
        try {
            platform.getCommandGateway().sendCommandAndWaitForAResponse(new MyCustomDomainBox.MyCustomCommand(false), context);
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyMeterInteraction("client.myconsumer.command.requests", requestsMeter);
        verifyMeterInteraction("client.myconsumer.command.errors", errorsMeter);
    }

    @Test
    public void checkMetricsPublication_onOverall_fromSuccessfulQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer globalInterceptorRequestsTimeTimer = registerSpyTimer(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME);
        final Timer globalQGRequestsTimeTimer = registerSpyTimer(queryGatewayMetricNames.requestsTime);
        final Meter globalRequestsMeter = registerSpyMeter(queryGatewayMetricNames.requests);

        reset(metricRegistry);

        // When
        platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(), Contexts.empty());

        // Then
        verifyTimerInteraction(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME, globalInterceptorRequestsTimeTimer);
        verifyTimerInteraction(queryGatewayMetricNames.requestsTime, globalQGRequestsTimeTimer);
        verifyMeterInteraction(queryGatewayMetricNames.requests, globalRequestsMeter);
    }

    @Test
    public void checkMetricsPublication_onOverall_fromFailedQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer globalInterceptorRequestsTimeTimer = registerSpyTimer(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME);
        final Timer globalQGRequestsTimeTimer = registerSpyTimer(queryGatewayMetricNames.requestsTime);
        final Meter globalRequestsMeter = registerSpyMeter(queryGatewayMetricNames.requests);
        final Meter globalErrorsMeter = registerSpyMeter(queryGatewayMetricNames.errors);

        reset(metricRegistry);

        // When
        try {
            platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(false), Contexts.empty());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME, globalInterceptorRequestsTimeTimer);
        verifyTimerInteraction(queryGatewayMetricNames.requestsTime, globalQGRequestsTimeTimer);
        verifyMeterInteraction(queryGatewayMetricNames.requests, globalRequestsMeter);
        verifyMeterInteraction(queryGatewayMetricNames.errors, globalErrorsMeter);
    }

    @Test
    public void checkMetricsPublication_onDomainPerTypeAndComponent_fromSuccessfulQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer interceptorRequestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.interceptor-requests-time");
        final Timer requestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.requests-handle-time");
        final Meter requestsMeter = registerSpyMeter("mycustomdomain.query.mycustomquery.requests");

        reset(metricRegistry);

        // When
        platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(), Contexts.empty());

        // Then
        verifyTimerInteraction("mycustomdomain.query.mycustomquery.interceptor-requests-time", interceptorRequestsTimeTimer);
        verifyTimerInteraction("mycustomdomain.query.mycustomquery.requests-handle-time", requestsTimeTimer);
        verifyMeterInteraction("mycustomdomain.query.mycustomquery.requests", requestsMeter);

    }

    @Test
    public void checkMetricsPublication_onDomainPerTypeAndComponent_fromFailedQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer interceptorRequestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.interceptor-requests-time");
        final Timer requestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.requests-handle-time");
        final Meter requestsMeter = registerSpyMeter("mycustomdomain.query.mycustomquery.requests");
        final Meter errorsMeter = registerSpyMeter("mycustomdomain.query.mycustomquery.errors");

        reset(metricRegistry);

        // When
        try {
            platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(false), Contexts.empty());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction("mycustomdomain.query.mycustomquery.interceptor-requests-time", interceptorRequestsTimeTimer);
        verifyTimerInteraction("mycustomdomain.query.mycustomquery.requests-handle-time", requestsTimeTimer);
        verifyMeterInteraction("mycustomdomain.query.mycustomquery.requests", requestsMeter);
        verifyMeterInteraction("mycustomdomain.query.mycustomquery.errors", errorsMeter);
    }

    @Test
    public void checkMetricsPublication_onDomainPerType_fromSuccessfulQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer domainRequestsTimeTimer = registerSpyTimer("mycustomdomain.query.requests-handle-time");
        final Meter domainRequestsMeter = registerSpyMeter("mycustomdomain.query.requests");

        reset(metricRegistry);

        // When
        platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(), Contexts.empty());

        // Then
        verifyTimerInteraction("mycustomdomain.query.requests-handle-time", domainRequestsTimeTimer);
        verifyMeterInteraction("mycustomdomain.query.requests", domainRequestsMeter);
    }

    @Test
    public void checkMetricsPublication_onDomainPerType_fromFailedQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer domainRequestsTimeTimer = registerSpyTimer("mycustomdomain.query.requests-handle-time");
        final Meter domainRequestsMeter = registerSpyMeter("mycustomdomain.query.requests");
        final Meter domainErrorsMeter = registerSpyMeter("mycustomdomain.query.errors");

        reset(metricRegistry);

        // When
        try {
            platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(false), Contexts.empty());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction("mycustomdomain.query.requests-handle-time", domainRequestsTimeTimer);
        verifyMeterInteraction("mycustomdomain.query.requests", domainRequestsMeter);
        verifyMeterInteraction("mycustomdomain.query.errors", domainErrorsMeter);
    }

    @Test
    public void checkMetricsPublication_onClientPerType_fromSuccessfulQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Meter requestMeter = registerSpyMeter("client.myconsumer.query.requests");

        reset(metricRegistry);

        final Context context = Contexts.builder().withClientId("myconsumer").build();

        // When
        platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(), context);

        // Then
        verifyMeterInteraction("client.myconsumer.query.requests", requestMeter);
    }

    @Test
    public void checkMetricsPublication_onClientPerType_fromFailedQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Meter requestsMeter = registerSpyMeter("client.myconsumer.query.requests");
        final Meter errorsMeter = registerSpyMeter("client.myconsumer.query.errors");

        reset(metricRegistry);

        final Context context = Contexts.builder().withClientId("myconsumer").build();

        // When
        try {
            platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(false), context);
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyMeterInteraction("client.myconsumer.query.requests", requestsMeter);
        verifyMeterInteraction("client.myconsumer.query.errors", errorsMeter);
    }

    private Timer registerSpyTimer(final String name) {
        final Timer.Context context = mock(Timer.Context.class);
        final Timer metered = mock(Timer.class);
        when(metered.time()).thenReturn(context);
        return metricRegistry.register(name, metered);
    }

    private Meter registerSpyMeter(final String name) {
        final Meter meter = mock(Meter.class);
        return metricRegistry.register(name, meter);
    }

    private void verifyTimerInteraction(final String name, final Timer metered) {
        verify(metricRegistry, times(1)).timer(name);
        verify(metered, times(1)).time();
        verifyNoMoreInteractions(metered);
    }

    private void verifyMeterInteraction(final String name, final Meter metered) {
        verify(metricRegistry, times(1)).meter(name);
        verify(metered, times(1)).mark();
        verifyNoMoreInteractions(metered);
    }

}
