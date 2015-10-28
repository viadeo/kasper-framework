// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;

/**
 * A class implements this interface in order to have the capability to be auto wired with the platform components.
 *
 * @param <COMMAND> the handled command class
 *
 * @see CommandHandler
 */
public interface WirableCommandHandler<COMMAND extends Command> extends CommandHandler<COMMAND> {

    /**
     * Wires an event bus on this <code>CommandHandler</code> instance.
     * @param eventBus an event bus
     */
    void setEventBus(EventBus eventBus);

    /**
     * Wires a repository manager on this <code>CommandHandler</code> instance.
     * @param repositoryManager a repository manager
     */
    void setRepositoryManager(RepositoryManager repositoryManager);

    /**
     * Wires a command gateway on this <code>CommandHandler</code> instance.
     * @param commandGateway a command gateway
     */
    void setCommandGateway(CommandGateway commandGateway);

}
