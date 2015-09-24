// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.netflix.hystrix.HystrixCommand;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResilienceInterceptorFactories {

    public static final String COMMAND_GROUP_NAME = "command";
    public static final String QUERY_GROUP_NAME = "query";

    private ResilienceInterceptorFactories() {}

    public static InterceptorFactory<Command, CommandResponse> forCommand(
            final MetricRegistry metricRegistry,
            final ResiliencePolicy policy,
            final ResilienceConfigurator configurer
    ) {
        return new InterceptorFactory<Command,CommandResponse>() {
            @Override
            public Optional<InterceptorChain<Command, CommandResponse>> create(final TypeToken<?> type) {
                checkNotNull(type);
                return Optional.of(InterceptorChain.makeChain(
                        new ResilienceInterceptor<Command,CommandResponse>(metricRegistry, policy, configurer) {
                            @Override
                            public CommandResponse fallback(
                                    final HystrixCommand<CommandResponse> command,
                                    final Exception exception
                            ) {
                                return CommandResponse.failure(createReason(type, command, exception));
                            }

                            @Override
                            public String getGroupName() {
                                return COMMAND_GROUP_NAME;
                            }
                        }
                ));
            }
        };
    }

    public static InterceptorFactory<Query,QueryResponse<QueryResult>> forQuery(
            final MetricRegistry metricRegistry,
            final ResiliencePolicy policy,
            final ResilienceConfigurator configurer
    ) {
        return new InterceptorFactory<Query,QueryResponse<QueryResult>>() {
            @Override
            public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
                checkNotNull(type);
                return Optional.of(InterceptorChain.makeChain(
                    new ResilienceInterceptor<Query,QueryResponse<QueryResult>>(metricRegistry, policy, configurer) {
                        @Override
                        public QueryResponse<QueryResult> fallback(
                                final HystrixCommand<QueryResponse<QueryResult>> command,
                                final Exception exception
                        ) {
                            return QueryResponse.failure(createReason(type, command, exception));
                        }

                        @Override
                        public String getGroupName() {
                            return QUERY_GROUP_NAME;
                        }
                    }
                ));
            }
        };
    }

    private static KasperReason createReason(
            final TypeToken<?> type,
            final HystrixCommand<?> command,
            final Exception exception
    ) {
        final String message;
        final CoreReasonCode coreReasonCode;

        if (command.isCircuitBreakerOpen()) {
            message = String.format("Circuit-breaker is open, <handler=%s>", type.getRawType().getName());
            coreReasonCode = CoreReasonCode.SERVICE_UNAVAILABLE;
        } else if (command.isFailedExecution()) {
            message = String.format("Failed to execute request, <handler=%s>", type.getRawType().getName());
            coreReasonCode = CoreReasonCode.INTERNAL_COMPONENT_ERROR;
        } else if (command.isResponseTimedOut()) {
            message = String.format("Error executing request due to a timed out, <handler=%s>", type.getRawType().getName());
            coreReasonCode = CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT;
        } else {
            message = String.format("Unexpected error, <handler=%s>", type.getRawType().getName());
            coreReasonCode = CoreReasonCode.UNKNOWN_REASON;
        }

        final KasperException kasperException;

        if (exception == null) {
            kasperException = new KasperException(message);
        } else {
            kasperException = new KasperException(message, exception);
        }

        return new KasperReason(coreReasonCode, kasperException);
    }

}
