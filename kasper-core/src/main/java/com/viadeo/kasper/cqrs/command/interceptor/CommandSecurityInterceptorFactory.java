// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.security.DefaultPublicSecurityStrategy;
import com.viadeo.kasper.security.DefaultSecurityStrategy;
import com.viadeo.kasper.security.SecurityConfiguration;
import com.viadeo.kasper.security.SecurityStrategy;
import com.viadeo.kasper.security.annotation.XKasperPublic;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandSecurityInterceptorFactory extends CommandInterceptorFactory {

    private SecurityConfiguration securityConfiguration;

    // ------------------------------------------------------------------------

    public CommandSecurityInterceptorFactory(final SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = checkNotNull(securityConfiguration);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Optional<InterceptorChain<Command, CommandResponse>> create(final TypeToken<?> type) {
        final Class<?> commandClass = checkNotNull(type).getRawType();

        final SecurityStrategy securityStrategy;

        if (commandClass.isAnnotationPresent(XKasperPublic.class)) {
            securityStrategy = new DefaultPublicSecurityStrategy(securityConfiguration);
        } else {
            securityStrategy = new DefaultSecurityStrategy(securityConfiguration);
        }

        final Interceptor<Command, CommandResponse> interceptor =
                new CommandSecurityInterceptor<>(securityStrategy);

        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

}
