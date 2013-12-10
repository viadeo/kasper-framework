package com.viadeo.kasper.event;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.CommandGateway;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CommandEventListener<E extends IEvent> extends EventListener<E> {

    private CommandGateway commandGateway;

    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = checkNotNull(commandGateway);
    }

    protected Optional<CommandGateway> getCommandGateway() {
        return Optional.fromNullable(this.commandGateway);
    }
}
