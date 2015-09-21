// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.interceptor;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandBus;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.unitofwork.UnitOfWork;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperCommandInterceptor implements org.axonframework.commandhandling.CommandHandlerInterceptor {

    private final InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry;
    private final KasperCommandBus commandBus;

    // ------------------------------------------------------------------------

    public KasperCommandInterceptor(final KasperCommandBus commandBus, final InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry) {
        this.commandBus = checkNotNull(commandBus);
        this.interceptorChainRegistry = checkNotNull(interceptorChainRegistry);
    }

    // ------------------------------------------------------------------------

    @Override
    public Object handle(final CommandMessage<?> commandMessage,
                         final UnitOfWork unitOfWork,
                         final org.axonframework.commandhandling.InterceptorChain axonInterceptorChain)
            throws Throwable {

        final Class<? extends CommandHandler> commandHandlerClassFor = commandBus.findCommandHandlerClassFor(commandMessage);

        final Optional<InterceptorChain<Command, CommandResponse>> optionalInterceptorChain = interceptorChainRegistry.get(commandHandlerClassFor);
        final InterceptorChain<Command, CommandResponse> chain = optionalInterceptorChain.get();
        final Context context = (Context) commandMessage.getMetaData().get(Context.METANAME);

        return chain.next(
                (Command) commandMessage.getPayload(),
                context
        );
    }

}
