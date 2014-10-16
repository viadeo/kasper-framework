package com.viadeo.kasper.cqrs.command.impl;

import org.axonframework.commandhandling.AsynchronousCommandBus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;

public class KasperCommandBus extends AsynchronousCommandBus {

    private final ConcurrentMap<String, CommandHandler<?>> subscriptions = new ConcurrentHashMap<String, CommandHandler<?>>();

    @Override
    public <T> void subscribe(String commandName, CommandHandler<? super T> handler) {
        subscriptions.put(commandName, handler);
        super.subscribe(commandName, handler);
    }

    @Override
    public <T> boolean unsubscribe(String commandName, CommandHandler<? super T> handler) {
        subscriptions.remove(commandName, handler);
        return super.unsubscribe(commandName, handler);
    }

    public Class<? extends CommandHandler> findCommandHandlerClassFor(CommandMessage<?> command) {
        final CommandHandler handler = subscriptions.get(command.getCommandName());
        if (handler == null) {
            throw new NoHandlerForCommandException(format("No handler was subscribed to command [%s]",
                    command.getCommandName()));
        }
        return handler.getClass();
    }
}
