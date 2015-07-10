// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.domain.event.Event;
import com.viadeo.kasper.cqrs.command.CommandGateway;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CommandEventListener<E extends Event> extends EventListener<E> {

    private CommandGateway commandGateway;

    // ------------------------------------------------------------------------

    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = checkNotNull(commandGateway);
    }

    // ------------------------------------------------------------------------

    protected Optional<CommandGateway> getCommandGateway() {
        return Optional.fromNullable(this.commandGateway);
    }

}
