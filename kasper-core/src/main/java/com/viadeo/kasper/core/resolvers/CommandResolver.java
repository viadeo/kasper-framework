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

import com.google.common.base.Optional;
import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.locators.DomainLocator;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommandResolver extends AbstractResolver<Command> {

    private DomainLocator domainLocator;
    private CommandHandlerResolver commandHandlerResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Command";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Command> clazz) {

        if ( ! Command.class.isAssignableFrom(checkNotNull(clazz))) {
            return Optional.absent();
        }

        if (DOMAINS_CACHE.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        if (null != domainLocator) {
            final Optional<CommandHandler> handler = domainLocator.getHandlerForCommandClass(clazz);

            if (handler.isPresent()) {
                final Optional<Class<? extends Domain>> domain =
                        commandHandlerResolver.getDomainClass(handler.get().getClass());
                if (domain.isPresent()) {
                    DOMAINS_CACHE.put(clazz, domain.get());
                    return domain;
                }
            }
        } else {
            return domainResolver.getDomainClassOf(clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends Command> commandClazz) {
 		final XKasperCommand annotation =
                checkNotNull(commandClazz).getAnnotation(XKasperCommand.class);

		// Get description ----------------------------------------------------
		String description = "";

        if (null != annotation) {
           description = annotation.description();
        }

		if (description.isEmpty()) {
			description = String.format("The %s command", this.getLabel(commandClazz));
		}

        return description;
    }

    public String getLabel(Class<? extends Command> commandClass) {
        return checkNotNull(commandClass).getSimpleName().replace("Command", "");
    }

    // ------------------------------------------------------------------------

    public void setDomainLocator(final DomainLocator domainLocator) {
        this.domainLocator = checkNotNull(domainLocator);
    }

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

}
