// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.gateway;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.CommandMessage;
import org.axonframework.repository.ConflictingAggregateVersionException;
import org.axonframework.unitofwork.UnitOfWork;

public class AxonCommandHandler<COMMAND extends Command> implements org.axonframework.commandhandling.CommandHandler<COMMAND> {

    private final CommandHandler<COMMAND> commandHandler;

    public AxonCommandHandler(CommandHandler<COMMAND> commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public Object handle(org.axonframework.commandhandling.CommandMessage<COMMAND> commandMessage, UnitOfWork unitOfWork) throws Throwable {
        final CommandMessage<COMMAND> message = new CommandMessage<>(commandMessage);
        final boolean isError;

        if (unitOfWork instanceof ContextualizedUnitOfWork) {
            ((ContextualizedUnitOfWork) unitOfWork).setContext(message.getContext());
        }

        Optional<Exception> exception = Optional.absent();
        CommandResponse response;

        try {
            response = commandHandler.handle(message);
        } catch (final ConflictingAggregateVersionException e) {
            response = CommandResponse.error(CoreReasonCode.CONFLICT, e.getMessage());
        } catch (Exception e) {
            response = CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
            exception = Optional.of(e);
        }

        switch (response.getStatus()) {
            case OK:
            case ACCEPTED:
            case SUCCESS:
                isError = false;
                break;
            case REFUSED:
            case ERROR:
            case FAILURE:
            default:
                isError = true;
        }

        /* rollback uow on failure */
        if (isError && unitOfWork.isStarted()) {
            if (exception.isPresent()) {
                unitOfWork.rollback(exception.get());
            } else {
                unitOfWork.rollback();
            }
            unitOfWork.start();
        }

        if (exception.isPresent()) {
            throw exception.get();
        }

        return response;
    }

    public CommandHandler<COMMAND> getDelegateHandler() {
        return commandHandler;
    }
}
