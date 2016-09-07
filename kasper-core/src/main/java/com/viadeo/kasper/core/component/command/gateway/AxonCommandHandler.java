// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
