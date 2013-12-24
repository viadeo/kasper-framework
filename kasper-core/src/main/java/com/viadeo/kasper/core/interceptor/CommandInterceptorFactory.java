// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;

public abstract class CommandInterceptorFactory implements InterceptorFactory<Command, CommandResponse> {

    @Override
    public boolean accept(TypeToken<?> type) {
        return CommandHandler.class.isAssignableFrom(type.getRawType());
    }
}
