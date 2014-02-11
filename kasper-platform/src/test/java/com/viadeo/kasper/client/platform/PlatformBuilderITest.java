// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.sample.MyCustomDomainBox;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import com.viadeo.kasper.cqrs.query.interceptor.QueryHandlerInterceptor;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlatformBuilderITest {

    private Platform platform;
    private MetricRegistry metricRegistry;

    @Before
    public void init() {
        metricRegistry = spy(new MetricRegistry());

        platform = new Platform.Builder(new KasperPlatformConfiguration())
                .withMetricRegistry(metricRegistry)
                .addDomainBundle(MyCustomDomainBox.getBundle())
                .build();

        // clear caches in order to ensure test integrity
        KasperMetrics.clearCache();
    }

    @Test
    public void checkMetricPublication_fromSuccessfulCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Meter globalRequestsNameMeter = registerSpyMeter(CommandHandler.GLOBAL_METER_REQUESTS_NAME);
        final Timer globalRequestsNameTimer = registerSpyTimer(CommandHandler.GLOBAL_TIMER_REQUESTS_TIME_NAME);

        final Meter requestsNameMeter = registerSpyMeter("mycustomdomain.command.mycustomcommand.requests");
        final Timer requestsNameTimer = registerSpyTimer("mycustomdomain.command.mycustomcommand.requests-time");

        reset(metricRegistry);

        // When
        platform.getCommandGateway().sendCommand(new MyCustomDomainBox.MyCustomCommand(), DefaultContextBuilder.get());

        // Then
        verifyTimerInteraction(CommandHandler.GLOBAL_TIMER_REQUESTS_TIME_NAME, globalRequestsNameTimer);
        verifyMeterInteraction(CommandHandler.GLOBAL_METER_REQUESTS_NAME, globalRequestsNameMeter);

        verifyTimerInteraction("mycustomdomain.command.mycustomcommand.requests-time", requestsNameTimer);
        verifyMeterInteraction("mycustomdomain.command.mycustomcommand.requests", requestsNameMeter);

        verifyNoMoreInteractions(metricRegistry);
    }

    @Test
    public void checkMetricPublication_fromFailedCommand_shouldPublishMetrics() throws Exception {
        // Given
        final Timer globalRequestsNameTimer = registerSpyTimer(CommandHandler.GLOBAL_TIMER_REQUESTS_TIME_NAME);
        final Meter globalRequestsNameMeter = registerSpyMeter(CommandHandler.GLOBAL_METER_REQUESTS_NAME);
        final Meter globalErrorsNameMeter = registerSpyMeter(CommandHandler.GLOBAL_METER_ERRORS_NAME);

        final Timer requestsNameTimer = registerSpyTimer("mycustomdomain.command.mycustomcommand.requests-time");
        final Meter requestsNameMeter = registerSpyMeter("mycustomdomain.command.mycustomcommand.requests");
        final Meter errorsNameMeter = registerSpyMeter("mycustomdomain.command.mycustomcommand.errors");

        reset(metricRegistry);

        // When
        try {
            platform.getCommandGateway().sendCommand(new MyCustomDomainBox.MyCustomCommand(false), DefaultContextBuilder.get());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction(CommandHandler.GLOBAL_TIMER_REQUESTS_TIME_NAME, globalRequestsNameTimer);
        verifyMeterInteraction(CommandHandler.GLOBAL_METER_REQUESTS_NAME, globalRequestsNameMeter);
        verifyMeterInteraction(CommandHandler.GLOBAL_METER_ERRORS_NAME, globalErrorsNameMeter);

        verifyTimerInteraction("mycustomdomain.command.mycustomcommand.requests-time", requestsNameTimer);
        verifyMeterInteraction("mycustomdomain.command.mycustomcommand.requests", requestsNameMeter);
        verifyMeterInteraction("mycustomdomain.command.mycustomcommand.errors", errorsNameMeter);

        verifyNoMoreInteractions(metricRegistry);
    }

    @Test
    public void checkMetricPublication_fromSuccessfulQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer globalInterceptorRequestsTimeTimer = registerSpyTimer(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME);
        final Timer globalQGRequestsTimeTimer = registerSpyTimer(KasperQueryGateway.GLOBAL_TIMER_REQUESTS_TIME_NAME);
        final Histogram globalQGRequestsTimesHisto = registerSpyHisto(KasperQueryGateway.GLOBAL_HISTO_REQUESTS_TIMES_NAME);
        final Meter globalRequestsMeter = registerSpyMeter(KasperQueryGateway.GLOBAL_METER_REQUESTS_NAME);

        final Timer interceptorRequestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.interceptor-requests-time");
        final Timer requestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.requests-time");
        final Histogram requestsTimesHisto = registerSpyHisto("mycustomdomain.query.mycustomquery.requests-times");
        final Meter requestsMeter = registerSpyMeter("mycustomdomain.query.mycustomquery.requests");

        reset(metricRegistry);

        // When
        platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(), DefaultContextBuilder.get());

        // Then
        verifyTimerInteraction(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME, globalInterceptorRequestsTimeTimer);
        verifyTimerInteraction(KasperQueryGateway.GLOBAL_TIMER_REQUESTS_TIME_NAME, globalQGRequestsTimeTimer);
        verifyHistogramInteraction(KasperQueryGateway.GLOBAL_HISTO_REQUESTS_TIMES_NAME, globalQGRequestsTimesHisto);
        verifyMeterInteraction(KasperQueryGateway.GLOBAL_METER_REQUESTS_NAME, globalRequestsMeter);

        verifyTimerInteraction("mycustomdomain.query.mycustomquery.interceptor-requests-time", interceptorRequestsTimeTimer);
        verifyTimerInteraction("mycustomdomain.query.mycustomquery.requests-time", requestsTimeTimer);
        verifyHistogramInteraction("mycustomdomain.query.mycustomquery.requests-times", requestsTimesHisto);
        verifyMeterInteraction("mycustomdomain.query.mycustomquery.requests", requestsMeter);

        verifyNoMoreInteractions(metricRegistry);
    }

    @Test
    public void checkMetricPublication_fromFailedQuery_shouldPublishMetrics() throws Exception {
        // Given
        final Timer globalInterceptorRequestsTimeTimer = registerSpyTimer(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME);
        final Timer globalQGRequestsTimeTimer = registerSpyTimer(KasperQueryGateway.GLOBAL_TIMER_REQUESTS_TIME_NAME);
        final Histogram globalQGRequestsTimesHisto = registerSpyHisto(KasperQueryGateway.GLOBAL_HISTO_REQUESTS_TIMES_NAME);
        final Meter globalRequestsMeter = registerSpyMeter(KasperQueryGateway.GLOBAL_METER_REQUESTS_NAME);
        final Meter globalErrorsMeter = registerSpyMeter(KasperQueryGateway.GLOBAL_METER_ERRORS_NAME);

        final Timer interceptorRequestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.interceptor-requests-time");
        final Timer requestsTimeTimer = registerSpyTimer("mycustomdomain.query.mycustomquery.requests-time");
        final Histogram requestsTimesHisto = registerSpyHisto("mycustomdomain.query.mycustomquery.requests-times");
        final Meter requestsMeter = registerSpyMeter("mycustomdomain.query.mycustomquery.requests");
        final Meter errorsMeter = registerSpyMeter("mycustomdomain.query.mycustomquery.errors");

        reset(metricRegistry);

        // When
        try {
            platform.getQueryGateway().retrieve(new MyCustomDomainBox.MyCustomQuery(false), DefaultContextBuilder.get());
        } catch (RuntimeException e) {
            // nothing
        }

        // Then
        verifyTimerInteraction(QueryHandlerInterceptor.GLOBAL_TIMER_INTERCEPTOR_REQUESTS_TIME_NAME, globalInterceptorRequestsTimeTimer);
        verifyTimerInteraction(KasperQueryGateway.GLOBAL_TIMER_REQUESTS_TIME_NAME, globalQGRequestsTimeTimer);
        verifyHistogramInteraction(KasperQueryGateway.GLOBAL_HISTO_REQUESTS_TIMES_NAME, globalQGRequestsTimesHisto);
        verifyMeterInteraction(KasperQueryGateway.GLOBAL_METER_REQUESTS_NAME, globalRequestsMeter);
        verifyMeterInteraction(KasperQueryGateway.GLOBAL_METER_ERRORS_NAME, globalErrorsMeter);

        verifyTimerInteraction("mycustomdomain.query.mycustomquery.interceptor-requests-time", interceptorRequestsTimeTimer);
        verifyTimerInteraction("mycustomdomain.query.mycustomquery.requests-time", requestsTimeTimer);
        verifyHistogramInteraction("mycustomdomain.query.mycustomquery.requests-times", requestsTimesHisto);
        verifyMeterInteraction("mycustomdomain.query.mycustomquery.requests", requestsMeter);
        verifyMeterInteraction("mycustomdomain.query.mycustomquery.errors", errorsMeter);

        verifyNoMoreInteractions(metricRegistry);
    }

    private Histogram registerSpyHisto(final String name) {
        final Histogram metered = mock(Histogram.class);
        return metricRegistry.register(name, metered);
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

    private void verifyHistogramInteraction(final String name, final Histogram metered) {
        verify(metricRegistry, times(1)).histogram(name);
        verify(metered, times(1)).update(anyLong());
        verifyNoMoreInteractions(metered);
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
