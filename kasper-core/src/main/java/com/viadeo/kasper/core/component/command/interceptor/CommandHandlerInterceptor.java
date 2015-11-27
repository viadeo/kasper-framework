// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.interceptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.command.gateway.AxonCommandHandler;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.axonframework.commandhandling.DefaultInterceptorChain;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.unitofwork.CurrentUnitOfWork;

import java.util.List;
import java.util.Map;

public class CommandHandlerInterceptor implements Interceptor<Command, CommandResponse> {

    private final AxonCommandHandler handler;

    public CommandHandlerInterceptor(final AxonCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public CommandResponse process(
            final Command command,
            final Context context,
            final InterceptorChain<Command, CommandResponse> chain
    ) {

        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, context);

        final GenericCommandMessage newCommandMessage =
                new GenericCommandMessage<>(command).withMetaData(metaData);

        try {
            final List<? extends org.axonframework.commandhandling.CommandHandlerInterceptor> interceptors = Lists.newArrayList();
            return CommandResponse.class.cast(
                    new DefaultInterceptorChain(newCommandMessage, CurrentUnitOfWork.get(), handler, interceptors).proceed()
            );
        } catch (final Throwable throwable) {
            throw new KasperException(throwable);
        }
    }

}
