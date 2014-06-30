// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.cqrs.command.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.security.annotation.XKasperPublic;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.strategy.SecurityStrategy;
import com.viadeo.kasper.security.strategy.impl.DefaultPublicSecurityStrategy;
import com.viadeo.kasper.security.strategy.impl.DefaultSecurityStrategy;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandSecurityInterceptorFactory extends CommandInterceptorFactory {

    private SecurityConfiguration securityConfiguration;
    private final Map<Class, SecurityStrategy> strategies = Maps.newHashMap();

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

        if (strategies.containsKey(commandClass)) {
            securityStrategy = strategies.get(commandClass);
        } else {
            if (commandClass.isAnnotationPresent(XKasperPublic.class)) {
                securityStrategy = new DefaultPublicSecurityStrategy(securityConfiguration, commandClass);
            } else {
                securityStrategy = new DefaultSecurityStrategy(securityConfiguration, commandClass);
            }
            strategies.put(commandClass, securityStrategy);
        }

        final Interceptor<Command, CommandResponse> interceptor =
                new CommandSecurityInterceptor<>(securityStrategy);

        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

}
