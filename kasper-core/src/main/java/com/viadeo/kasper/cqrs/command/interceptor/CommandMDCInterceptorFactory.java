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
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.interceptor.MDCInterceptor;

public class CommandMDCInterceptorFactory extends CommandInterceptorFactory {

    private final MDCInterceptor<Command,CommandResponse> interceptor = new MDCInterceptor<>();

    // ------------------------------------------------------------------------

    @Override
    public Optional<InterceptorChain<Command, CommandResponse>> create(final TypeToken<?> type) {
        return Optional.of(InterceptorChain.makeChain(interceptor));
    }

}
