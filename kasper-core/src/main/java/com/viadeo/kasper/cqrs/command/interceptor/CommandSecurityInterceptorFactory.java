package com.viadeo.kasper.cqrs.command.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.SecurityInterceptor;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.security.SecurityConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandSecurityInterceptorFactory extends CommandInterceptorFactory {
    private SecurityConfiguration securityConfiguration;

    public CommandSecurityInterceptorFactory(SecurityConfiguration securityConfiguration) {
        checkNotNull(securityConfiguration);
        this.securityConfiguration = securityConfiguration;
    }

    @Override
    public Optional<InterceptorChain<Command, Command>> create(TypeToken<?> type) {
        final Interceptor<Command, Command> interceptor;

        interceptor = new SecurityInterceptor<Command, Command>(securityConfiguration);
        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

}
