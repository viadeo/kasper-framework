// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.netflix.hystrix.*;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ResilienceInterceptor<INPUT, OUTPUT extends KasperResponse> implements Interceptor<INPUT, OUTPUT> {

    private static final String UNKNOWN_GROUP_NAME = "unknown";

    private final ResiliencePolicy policy;

    public ResilienceInterceptor(final MetricRegistry metricRegistry, final ResiliencePolicy policy) {
        this.policy = checkNotNull(policy);
        registerMetricsPublisherOnHystrix(checkNotNull(metricRegistry), HystrixPlugins.getInstance());
    }

    @Override
    public OUTPUT process(final INPUT input, final Context context, final InterceptorChain<INPUT, OUTPUT> chain) throws Exception {
        try {
            return new HystrixCommand<OUTPUT>(from(input)) {

                private Exception exception;

                @Override
                protected OUTPUT run() throws Exception {
                    try {
                        return chain.next(input, context);
                    } catch (Exception e) {
                        policy.manage(e);
                        exception = e;
                        throw e;
                    }
                }

                @Override
                protected OUTPUT getFallback() {
                    return fallback(this, exception);
                }

            }.execute();

        } catch (HystrixBadRequestException e) {
            throw (Exception) e.getCause();
        }
    }

    public abstract OUTPUT fallback(final HystrixCommand<OUTPUT> command, final Exception exception);
    public abstract String getGroupName();

    protected HystrixCommand.Setter from(final INPUT input) {
        checkNotNull(input);

        final String inputName = input.getClass().getName();
        final String groupName = Objects.firstNonNull(getGroupName(), UNKNOWN_GROUP_NAME);

        return HystrixCommand.Setter
                // configure command group and command name
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupName))
                .andCommandKey(HystrixCommandKey.Factory.asKey(inputName))
                // configure thread pool key
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(input.getClass().getPackage().getName()))
                // configure circuit breaker
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                // TODO retrieve this values from config
                                .withExecutionIsolationThreadTimeoutInMilliseconds(1000) // default 1000 ms
                                .withCircuitBreakerSleepWindowInMilliseconds(60000) // default to 500 ms
                                .withMetricsRollingStatisticalWindowInMilliseconds(60000) // default to 10000 ms
                                .withMetricsRollingStatisticalWindowBuckets(60) // default to 10
                );
    }

    @VisibleForTesting
    protected static boolean metricInitialized;

    @VisibleForTesting
    protected static void registerMetricsPublisherOnHystrix(
            final MetricRegistry metricRegistry,
            final HystrixPlugins hystrixPlugins
    ) {
        if ( ! metricInitialized && metricRegistry != null) {
            hystrixPlugins.registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(metricRegistry));
            metricInitialized = true;
        }
    }
}
