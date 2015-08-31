// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.viadeo.kasper.core.component.command.gateway.AxonCommandHandler;
import org.axonframework.commandhandling.AsynchronousCommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;

public class KasperCommandBus extends AsynchronousCommandBus {

    private final ConcurrentMap<String, CommandHandler<?>> subscriptions = new ConcurrentHashMap<String, CommandHandler<?>>();

    // ------------------------------------------------------------------------

    @Override
    public <T> void subscribe(final String commandName, final CommandHandler<? super T> handler) {
        subscriptions.put(commandName, handler);
        super.subscribe(commandName, handler);
    }

    @Override
    public <T> boolean unsubscribe(final String commandName, final CommandHandler<? super T> handler) {
        subscriptions.remove(commandName, handler);
        return super.unsubscribe(commandName, handler);
    }

    public Class<? extends CommandHandler> findCommandHandlerClassFor(final CommandMessage<?> command) {
        final CommandHandler handler = subscriptions.get(command.getCommandName());
        if (null == handler) {
            throw new NoHandlerForCommandException(format(
                    "No handler was subscribed to command [%s]",
                    command.getCommandName()
            ));
        }
        if (handler instanceof AxonCommandHandler) {
            return ((AxonCommandHandler)handler).getDelegateHandler().getHandlerClass();
        }
        return handler.getClass();
    }

}
