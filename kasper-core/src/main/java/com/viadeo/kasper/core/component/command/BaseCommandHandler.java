// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;

public abstract class BaseCommandHandler<COMMAND extends Command> implements CommandHandler<COMMAND> {

    private final Class<COMMAND> commandClass;

    protected BaseCommandHandler() {
        @SuppressWarnings("unchecked")
        final Optional<Class<COMMAND>> commandClass =
                (Optional<Class<COMMAND>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                        this.getClass(),
                        BaseCommandHandler.class,
                        BaseCommandHandler.COMMAND_PARAMETER_POSITION)
                );

        if ( ! commandClass.isPresent()) {
            throw new KasperCommandException(
                    "Unable to determine Command class for "
                            + this.getClass().getSimpleName()
            );
        }

        this.commandClass = commandClass.get();
    }

    @Override
    public Class<COMMAND> getInputClass() {
        return commandClass;
    }

    @Override
    public Class<? extends CommandHandler> getHandlerClass() {
        return this.getClass();
    }
}
