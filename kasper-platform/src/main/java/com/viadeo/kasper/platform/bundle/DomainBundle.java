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
package com.viadeo.kasper.platform.bundle;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.platform.builder.PlatformContext;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The DomainBundle interface represents a domain with its components like : command handlers, query handlers, event
 * listeners and repositories.
 *
 * A bundle can be configured in order to finalize the initialization for its components.
 *
 * Note that the components returned by this object will be wired during the construction of the platform.
 */
public interface DomainBundle {

    /**
     * Configure the bundle with the context of the builder in order to access to the components of the platform.
     *
     * @param context the context of the builder
     */
    void configure(PlatformContext context);

    /**
     * @return the name of the domain
     */
    String getName();

    /**
     * @return the domain
     */
    Domain getDomain();

    /**
     * @return all command handlers identified as components of this domain bundle
     */
    List<CommandHandler> getCommandHandlers();

    /**
     * @return all query handlers identified as components of this domain bundle
     */
    List<QueryHandler> getQueryHandlers();

    /**
     * @return all event listeners identified as components of this domain bundle
     */
    List<EventListener> getEventListeners();

    /**
     * @return all repositories identified as components of this domain bundle
     */
    List<Repository> getRepositories();

    /**
     * @return all query interceptor factories of this domain bundle
     */
    List<QueryInterceptorFactory> getQueryInterceptorFactories();

    /**
     * @return all command interceptor factories of this domain bundle
     */
    List<CommandInterceptorFactory> getCommandInterceptorFactories();

    /**
     * @return all sagas identified as components of this domain bundle
     */
    List<Saga> getSagas();

    /**
     * @return all event interceptor factories of this domain bundle
     */
    List<EventInterceptorFactory> getEventInterceptorFactories();

    // ========================================================================

    class Builder {

        private final Domain domain;
        private final String domainName;

        private final List<CommandHandler> commandHandlers = Lists.newArrayList();
        private final List<QueryHandler> queryHandlers = Lists.newArrayList();
        private final List<EventListener> eventListeners = Lists.newArrayList();
        private final List<Saga> sagas = Lists.newArrayList();
        private final List<Repository> repositories = Lists.newArrayList();
        private final List<QueryInterceptorFactory> queryInterceptorFactories = Lists.newArrayList();
        private final List<CommandInterceptorFactory> commandInterceptorFactories = Lists.newArrayList();
        private final List<EventInterceptorFactory> eventInterceptorFactories = Lists.newArrayList();

        // --------------------------------------------------------------------

        public Builder(final Domain domain){
            this.domain = checkNotNull(domain);
            this.domainName = new DomainResolver().getLabel(domain.getClass());
        }

        // --------------------------------------------------------------------

        public Builder with(final CommandHandler commandHandler, final CommandHandler... commandHandlers){
            this.commandHandlers.add(checkNotNull(commandHandler));
            with(this.commandHandlers, commandHandlers);
            return this;
        }

        public Builder with(final QueryHandler queryHandler, final QueryHandler... queryHandlers){
            this.queryHandlers.add(checkNotNull(queryHandler));
            with(this.queryHandlers, queryHandlers);
            return this;
        }

        public Builder with(final EventListener eventListener, final EventListener... eventListeners){
            this.eventListeners.add(checkNotNull(eventListener));
            with(this.eventListeners, eventListeners);
            return this;
        }

        public Builder with(final Saga saga, final Saga... sagas){
            this.sagas.add(checkNotNull(saga));
            with(this.sagas, sagas);
            return this;
        }

        public Builder with(final Repository repository, final Repository... repositories){
            this.repositories.add(checkNotNull(repository));
            with(this.repositories, repositories);
            return this;
        }

        public Builder with(final QueryInterceptorFactory factory, final QueryInterceptorFactory... factories){
            this.queryInterceptorFactories.add(checkNotNull(factory));
            with(this.queryInterceptorFactories, factories);
            return this;
        }

        public Builder with(final CommandInterceptorFactory factory, final CommandInterceptorFactory... factories){
            this.commandInterceptorFactories.add(checkNotNull(factory));
            with(this.commandInterceptorFactories, factories);
            return this;
        }

        public Builder with(final EventInterceptorFactory factory, final EventInterceptorFactory... factories){
            this.eventInterceptorFactories.add(checkNotNull(factory));
            with(this.eventInterceptorFactories, factories);
            return this;
        }

        @SafeVarargs
        private final <COMP> void with(final List<COMP> collection, final COMP... components) {
            checkNotNull(collection);
            for (final COMP component : checkNotNull(components)) {
                collection.add(checkNotNull(component));
            }
        }

        public DomainBundle build() {
            return new DefaultDomainBundle(
                    commandHandlers,
                    queryHandlers,
                    repositories,
                    eventListeners,
                    sagas,
                    queryInterceptorFactories,
                    commandInterceptorFactories,
                    eventInterceptorFactories,
                    domain,
                    domainName
            );
        }

    }

}
