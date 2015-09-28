// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authc;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.annotation.XKasperPublic;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthenticationInterceptor<I, O> implements Interceptor<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private final Class<?> target;
    private final Authenticator authenticator;
    private final MetricRegistry metricRegistry;

    public AuthenticationInterceptor(final MetricRegistry metricRegistry, final TypeToken type, final Authenticator authenticator) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.target = checkNotNull(type).getRawType();
        this.authenticator = checkNotNull(authenticator);
    }

    public static class Factories {

        public Factories() {
        }

        public static CommandInterceptorFactory forCommand(
                final MetricRegistry metricRegistry,
                final Authenticator authenticator
        ) {
            return new CommandInterceptorFactory() {
                @Override
                public Optional<InterceptorChain<Command, CommandResponse>> create(final TypeToken<?> type) {
                    checkNotNull(type);
                    return Optional.of(InterceptorChain.makeChain(
                            new AuthenticationInterceptor<Command, CommandResponse>(metricRegistry, type, authenticator)
                    ));
                }
            };
        }

        public static QueryInterceptorFactory forQuery(
                final MetricRegistry metricRegistry,
                final Authenticator authenticator
        ) {
            return new QueryInterceptorFactory() {
                @Override
                public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
                    checkNotNull(type);
                    return Optional.of(InterceptorChain.makeChain(
                            new AuthenticationInterceptor<Query, QueryResponse<QueryResult>>(metricRegistry, type, authenticator)
                    ));
                }
            };
        }
    }

    @Override
    public O process(I c, Context context, InterceptorChain<I, O> chain) throws Exception {
        checkNotNull(context);

        final boolean annotationPresent = target.isAnnotationPresent(XKasperPublic.class);
        if (annotationPresent) {
            return chain.next(c, context);
        }

        if (!authenticator.isAuthenticated(context)) {
            LOGGER.error("Invalid authentication for : {}", target.getName());
            throw new KasperInvalidAuthenticationException(
                    "Invalid authentication for : " + target.getName(), CoreReasonCode.INVALID_AUTHENTICATION
            );
        }

        return chain.next(c, context);
    }

}
