// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.GenericCommandMessage;

import java.util.Map;

public class CommandHandlerInterceptor implements Interceptor<Command, CommandResponse> {

    private final ThreadLocal<org.axonframework.commandhandling.InterceptorChain> axonInterceptorChain;

    public CommandHandlerInterceptor() {
        this.axonInterceptorChain = new ThreadLocal<>();
    }

    @Override
    public CommandResponse process(Command command, Context context, InterceptorChain<Command, CommandResponse> chain) throws Exception {
        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, context);

        final GenericCommandMessage newCommandMessage = new GenericCommandMessage<>(command).withMetaData(metaData);

        try {
            org.axonframework.commandhandling.InterceptorChain interceptorChain = axonInterceptorChain.get();
            return CommandResponse.class.cast(interceptorChain.proceed(newCommandMessage));
        } catch (Throwable throwable) {
            throw new KasperException(throwable);
        }
    }

    public void set(org.axonframework.commandhandling.InterceptorChain axonInterceptorChain) {
        this.axonInterceptorChain.set(axonInterceptorChain);
    }

    public void remove() {
        this.axonInterceptorChain.remove();
    }
}
