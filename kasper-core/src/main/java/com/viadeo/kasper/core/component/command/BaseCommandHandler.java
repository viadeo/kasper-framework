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
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;

public abstract class BaseCommandHandler<COMMAND extends Command> implements CommandHandler<COMMAND> {

    private final Class<COMMAND> commandClass;

    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse handle(final CommandMessage<COMMAND> message) {
        try {
            return handle(message.getCommand());
        } catch (final UnsupportedOperationException e) {
            try {
                return handle(message.getContext(), message.getCommand());
            } catch (final UnsupportedOperationException e1) {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Handle the <code>Command</code> with his <code>Context</code>.
     *
     * @param context the context related to the request
     * @param command the command requested
     * @return a response
     */
    public CommandResponse handle(final Context context, final COMMAND command) {
        throw new UnsupportedOperationException("not yet implemented!");
    }

    /**
     * Handle the <code>Command</code> with his <code>Context</code>.
     *
     * @param command the command requested
     * @return a response
     */
    public CommandResponse handle(final COMMAND command) {
        throw new UnsupportedOperationException("not yet implemented!");
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
