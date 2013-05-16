package com.viadeo.kasper.locators;

import java.util.Collection;
import com.viadeo.kasper.cqrs.command.ICommand;
import com.viadeo.kasper.cqrs.command.ICommandHandler;

public interface ICommandHandlersLocator {
    
    void registerHandler(ICommandHandler<? extends ICommand> commandHandler);

    /**
     * Get all registered command handlers
     */
    Collection<ICommandHandler<? extends ICommand>> getHandlers();
}
