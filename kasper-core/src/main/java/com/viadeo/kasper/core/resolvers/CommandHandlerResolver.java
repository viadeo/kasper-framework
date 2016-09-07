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
package com.viadeo.kasper.core.resolvers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Map.Entry;

public class CommandHandlerResolver extends AbstractResolver<CommandHandler> {

    private static ConcurrentMap<Class, Class> cacheCommands = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "CommandHandler";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends CommandHandler> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperCommandHandler handlerAnnotation = clazz.getAnnotation(XKasperCommandHandler.class);
        if (null != handlerAnnotation) {
            final Class<? extends Domain> domain = handlerAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends CommandHandler> handlerClazz) {
        final XKasperCommandHandler annotation =
                checkNotNull(handlerClazz).getAnnotation(XKasperCommandHandler.class);

        String description = "";
        if (null != annotation) {
            description = annotation.description();
        }
        if (description.isEmpty()) {
            description = String.format("The %s command handler", this.getLabel(handlerClazz));
        }

        return description;
    }

    @Override
    public String getLabel(final Class<? extends CommandHandler> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("CommandHandler", "")
                .replace("Handler", "")
                .replace("QueryService", "")
                .replace("Service", "");
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Command> getCommandClass(final Class<? extends CommandHandler> clazz) {

        if (cacheCommands.containsKey(checkNotNull(clazz))) {
            return cacheCommands.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Command>> commandClazz =
                (Optional<Class<? extends Command>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                CommandHandler.class,
                                CommandHandler.COMMAND_PARAMETER_POSITION
                        );

        if ( ! commandClazz.isPresent()) {
            throw new KasperException("Unable to find command type for handler " + clazz.getClass());
        }

        putCommandClass(clazz, commandClazz);

        return commandClazz.get();
    }

    @VisibleForTesting
    protected void putCommandClass(final Class<? extends CommandHandler> clazz, final Optional<Class<? extends Command>> commandClazz) {
        checkNotNull(clazz);
        checkNotNull(commandClazz);
        checkArgument(commandClazz.isPresent());
        cacheCommands.put(clazz, commandClazz.get());
    }

    @SuppressWarnings("unchecked")
    // @javax.annotation.Nullable
    public Optional<Class<? extends CommandHandler>> getHandlerClass(final Class<? extends Command> commandClass) {
        checkNotNull(commandClass);
        for (final Entry<Class, Class> handlerToCommand : cacheCommands.entrySet()) {
            if (commandClass.equals(handlerToCommand.getValue())) {
                return Optional.<Class<? extends CommandHandler>>of(handlerToCommand.getKey());
            }
        }
        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        super.clearCache();
        cacheCommands.clear();
    }

}
