package com.viadeo.kasper.client.platform.domain.descriptor;

import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;

public class CommandHandlerDescriptor implements Descriptor {
    private final Class<? extends CommandHandler> commandHandlerClass;
    private final Class<? extends Command> commandClass;

    public CommandHandlerDescriptor(Class<? extends CommandHandler> commandHandlerClass, Class<? extends Command> commandClass) {
        this.commandHandlerClass = commandHandlerClass;
        this.commandClass = commandClass;
    }

    @Override
    public Class<? extends CommandHandler> getReferenceClass() {
        return commandHandlerClass;
    }

    public Class<? extends Command> getCommandClass() {
        return commandClass;
    }
}