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
package com.viadeo.kasper.core.interceptor.resilience;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.netflix.hystrix.*;
import com.netflix.hystrix.contrib.codahalemetricspublisher.HystrixCodaHaleMetricsPublisher;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.UnitOfWork;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ResilienceInterceptor<INPUT, OUTPUT extends KasperResponse> implements Interceptor<INPUT, OUTPUT> {

    private static final String UNKNOWN_GROUP_NAME = "unknown";

    private final ResiliencePolicy policy;
    private final ResilienceConfigurator configurer;

    // ------------------------------------------------------------------------

    public ResilienceInterceptor(
            final MetricRegistry metricRegistry,
            final ResiliencePolicy policy,
            final ResilienceConfigurator configurer
    ) {
        this.policy = checkNotNull(policy);
        this.configurer = checkNotNull(configurer);
        registerMetricsPublisherOnHystrix(checkNotNull(metricRegistry), HystrixPlugins.getInstance(), configurer);
    }

    // ------------------------------------------------------------------------

    @Override
    public OUTPUT process(
            final INPUT input,
            final Context context,
            final InterceptorChain<INPUT, OUTPUT> chain
    ) {
        try {
            final Optional<UnitOfWork> currentUnitOfWork;

            if (!CurrentUnitOfWork.isStarted()) {
                currentUnitOfWork = Optional.absent();
            } else {
                currentUnitOfWork = Optional.of(CurrentUnitOfWork.get());
            }

            return new HystrixCommand<OUTPUT>(from(input)) {

                private Exception exception;

                @Override
                protected OUTPUT run() throws Exception {
                    if (currentUnitOfWork.isPresent()) {
                        CurrentUnitOfWork.set(currentUnitOfWork.get());
                    }

                    try {
                        return chain.next(input, context);
                    } catch (final Exception e) {
                        try {
                            policy.manage(e);
                        } catch (final Exception pe) {
                            exception = pe;
                            throw pe;
                        }
                        exception = e;
                        throw e;
                    }
                    finally {
                        if (currentUnitOfWork.isPresent()) {
                            CurrentUnitOfWork.clear(currentUnitOfWork.get());
                        }
                    }
                }

                @Override
                protected OUTPUT getFallback() {
                    return fallback(this, exception);
                }

            }.execute();

        } catch (final HystrixBadRequestException e) {
            throw (RuntimeException) e.getCause();
        }
    }

    // ------------------------------------------------------------------------

    public abstract OUTPUT fallback(final HystrixCommand<OUTPUT> command, final Exception exception);
    public abstract String getGroupName();

    // ------------------------------------------------------------------------

    protected HystrixCommand.Setter from(final INPUT input) {
        checkNotNull(input);

        final String inputName = input.getClass().getName();
        final String groupName = MoreObjects.firstNonNull(getGroupName(), UNKNOWN_GROUP_NAME);

        final ResilienceConfigurator.InputConfig config = configurer.configure(input);

        return HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupName))
                .andCommandKey(HystrixCommandKey.Factory.asKey(inputName))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(input.getClass().getName()))

                //TODO review values for the following thread pool properties in order to avoid to get a RejectedExecutionException
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(config.threadPoolCoreSize)
                                .withQueueSizeRejectionThreshold(config.threadPoolQueueSizeRejectionThreshold)
                )

                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerEnabled(config.circuitBreakerEnable)
                                .withCircuitBreakerSleepWindowInMilliseconds(config.circuitBreakerSleepWindowInMillis)
                                .withCircuitBreakerErrorThresholdPercentage(config.circuitBreakerThresholdInPercent)
                                .withCircuitBreakerRequestVolumeThreshold(config.circuitBreakerRequestVolumeThreshold)

                                .withExecutionIsolationThreadTimeoutInMilliseconds(config.executionTimeoutInMillis)

                                .withMetricsRollingStatisticalWindowInMilliseconds(60000) // default to 10000 ms
                                .withMetricsRollingStatisticalWindowBuckets(60) // default to 10
                );
    }

    @VisibleForTesting
    protected static boolean metricInitialized;

    @VisibleForTesting
    protected static void registerMetricsPublisherOnHystrix(
            final MetricRegistry metricRegistry,
            final HystrixPlugins hystrixPlugins,
            final ResilienceConfigurator configurer
    ) {
        if ( ! metricInitialized && null != metricRegistry && configurer.isHystrixMetricEnable() ) {
            hystrixPlugins.registerMetricsPublisher(new HystrixCodaHaleMetricsPublisher(metricRegistry));
            metricInitialized = true;
        }
    }

}
