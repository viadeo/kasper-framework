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
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.api.domain.command.CommandResponse;

public class CommandHandlerInterceptorFactory extends CommandInterceptorFactory {

    @Override
    public Optional<InterceptorChain<Command, CommandResponse>> create(final TypeToken<?> type) {
        return Optional.of(new com.viadeo.kasper.core.interceptor.InterceptorChain<>(
                new CommandHandlerInterceptor( /* axonInterceptorChain */ ))
        );
    }

}
