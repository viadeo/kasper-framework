// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authentication;

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

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.api.component.command.CommandResponse.DoAuthenticateCommandResponse;

public class AuthenticationInterceptor<I, O> implements Interceptor<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private final Class<?> target;
    private final Authenticator authenticator;

    public AuthenticationInterceptor(
            final TypeToken type,
            final Authenticator authenticator
    ) {
        final boolean isPublicHandler = checkNotNull(type).getRawType().isAnnotationPresent(XKasperPublic.class);
        this.target = checkNotNull(type).getRawType();
        this.authenticator = isPublicHandler ? new NoAuthentication() : checkNotNull(authenticator);
    }

    @Override
    public O process(final I c, final Context context, final InterceptorChain<I, O> chain) {
        checkAuthentication(context);
        return enrichResponseWithToken(chain.next(c, context));
    }

    protected void checkAuthentication(final Context context) {
        checkNotNull(context);
        if (!authenticator.isAuthenticated(context)) {
            LOGGER.error("Invalid authentication for : {}", target.getName());
            throw new KasperInvalidAuthenticationException(
                    "Invalid authentication for : " + target.getName(), CoreReasonCode.INVALID_AUTHENTICATION
            );
        }
    }

    protected O enrichResponseWithToken(final O output) {
        return output;
    }

    // ------------------------------------------------------------------------

    public static final class Factories {

        private Factories() {}

        public static CommandInterceptorFactory forCommand(
                final Authenticator authenticator,
                final AuthenticationTokenGenerator<String> authenticationTokenGenerator
        ) {
            return new CommandInterceptorFactory() {
                @Override
                public Optional<InterceptorChain<Command, CommandResponse>> create(final TypeToken<?> type) {
                    return Optional.of(InterceptorChain.makeChain(
                            new AuthenticationInterceptor<Command, CommandResponse>(type, authenticator) {

                                @Override
                                protected CommandResponse enrichResponseWithToken(final CommandResponse response) {
                                    if (response instanceof DoAuthenticateCommandResponse) {
                                        final DoAuthenticateCommandResponse doAuthenticateCommandResponse =
                                                (DoAuthenticateCommandResponse) response;
                                        try {
                                            final Serializable token = authenticationTokenGenerator.generate(
                                                    doAuthenticateCommandResponse.getSubjectID(),
                                                    doAuthenticateCommandResponse.getProperties()
                                            );
                                            return new AuthenticatedCommandResponse<>(response, token);
                                        } catch (final Exception e){
                                            LOGGER.error("Could not create AuthenticationToken",e);
                                        }
                                    }
                                    return response;
                                }
                            }
                    ));
                }
            };
        }

        public static QueryInterceptorFactory forQuery(
                final Authenticator authenticator,
                final AuthenticationTokenGenerator<String> authenticationTokenGenerator
        ) {
            return new QueryInterceptorFactory() {
                @Override
                public Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> create(final TypeToken<?> type) {
                    return Optional.of(InterceptorChain.makeChain(
                            new AuthenticationInterceptor<Query, QueryResponse<QueryResult>>(type, authenticator)
                    ));
                }
            };
        }
    }

    static class AuthenticatedCommandResponse<TOKEN extends Serializable> extends CommandResponse {
        public AuthenticatedCommandResponse(CommandResponse response, TOKEN token) {
            super(response);
            withAuthenticationToken(token);
        }
    }
}
