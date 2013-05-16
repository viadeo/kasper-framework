package com.viadeo.kasper.locators.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;
import com.viadeo.kasper.locators.ICommandHandlersLocator;

public class CommandHandlersLocatorBase implements ICommandHandlersLocator {
    private final List<ICommandHandler<? extends ICommand>> handlers = new ArrayList<ICommandHandler<? extends ICommand>>();
    
    @Override
    public void registerHandler(ICommandHandler<? extends ICommand> commandHandler) {
        handlers.add(commandHandler);
    }

    @Override
    public Collection<ICommandHandler<? extends ICommand>> getHandlers() {
        return Collections.unmodifiableCollection(handlers);
    }

}
