// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.api.domain.command.Command;
import com.viadeo.kasper.api.domain.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandBus;
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

        final InterceptorChain<Command, CommandResponse> chain;
        final Optional<InterceptorChain<Command, CommandResponse>> optionalInterceptorChain = interceptorChainRegistry.get(commandMessage.getPayloadType());

        if (optionalInterceptorChain.isPresent()) {
            chain = optionalInterceptorChain.get();
        } else {
            chain = interceptorChainRegistry.create(
                    commandBus.findCommandHandlerClassFor(commandMessage),
                    new CommandHandlerInterceptorFactory()
            ).get();
        }

        final Context context = (Context) commandMessage.getMetaData().get(Context.METANAME);
        final CommandHandlerInterceptor tail = (CommandHandlerInterceptor) chain.last().get();

        try {
            tail.set(axonInterceptorChain);
            return chain.next(
                (Command) commandMessage.getPayload(),
                context
            );
        } finally {
            tail.remove();
        }
    }

}
