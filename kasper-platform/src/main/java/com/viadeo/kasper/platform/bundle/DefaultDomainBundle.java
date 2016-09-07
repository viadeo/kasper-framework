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

public class DefaultDomainBundle implements DomainBundle {

    protected final List<CommandHandler> commandHandlers;
    protected final List<QueryHandler> queryHandlers;
    protected final List<Repository> repositories;
    protected final List<EventListener> eventListeners;
    protected final List<Saga> sagas;
    protected final List<QueryInterceptorFactory> queryInterceptorFactories;
    protected final List<CommandInterceptorFactory> commandInterceptorFactories;
    protected final List<EventInterceptorFactory> eventInterceptorFactories;
    protected final Domain domain;
    protected final String name;

    // ------------------------------------------------------------------------

    public DefaultDomainBundle(final Domain domain) {
        this(domain, new DomainResolver().getLabel(domain.getClass()));
    }

    public DefaultDomainBundle(final Domain domain, final String name) {
        this(Lists.<CommandHandler>newArrayList(),
             Lists.<QueryHandler>newArrayList(),
             Lists.<Repository>newArrayList(),
             Lists.<EventListener>newArrayList(),
             Lists.<Saga>newArrayList(),
             Lists.<QueryInterceptorFactory>newArrayList(),
             Lists.<CommandInterceptorFactory>newArrayList(),
             Lists.<EventInterceptorFactory>newArrayList(),
             domain,
             name
        );
    }

    public DefaultDomainBundle(final List<CommandHandler> commandHandlers,
                               final List<QueryHandler> queryHandlers,
                               final List<Repository> repositories,
                               final List<EventListener> eventListeners,
                               final List<Saga> sagas,
                               final List<QueryInterceptorFactory> queryInterceptorFactories,
                               final List<CommandInterceptorFactory> commandInterceptorFactories,
                               final List<EventInterceptorFactory> eventInterceptorFactories,
                               final Domain domain,
                               final String name) {
        this.commandHandlers = checkNotNull(commandHandlers);
        this.queryHandlers = checkNotNull(queryHandlers);
        this.repositories = checkNotNull(repositories);
        this.eventListeners = checkNotNull(eventListeners);
        this.sagas = checkNotNull(sagas);
        this.queryInterceptorFactories = checkNotNull(queryInterceptorFactories);
        this.commandInterceptorFactories = checkNotNull(commandInterceptorFactories);
        this.eventInterceptorFactories = checkNotNull(eventInterceptorFactories);
        this.domain = checkNotNull(domain);
        this.name = checkNotNull(name);
    }

    // ------------------------------------------------------------------------

    @Override
    public void configure(final PlatformContext context) { }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    @Override
    public List<CommandHandler> getCommandHandlers() {
        return commandHandlers;
    }

    @Override
    public List<QueryHandler> getQueryHandlers() {
        return queryHandlers;
    }

    @Override
    public List<EventListener> getEventListeners() {
        return eventListeners;
    }

    @Override
    public List<Repository> getRepositories() {
        return repositories;
    }

    @Override
    public List<QueryInterceptorFactory> getQueryInterceptorFactories() {
        return queryInterceptorFactories;
    }

    @Override
    public List<CommandInterceptorFactory> getCommandInterceptorFactories() {
        return commandInterceptorFactories;
    }

    @Override
    public List<EventInterceptorFactory> getEventInterceptorFactories() {
        return eventInterceptorFactories;
    }

    @Override
    public List<Saga> getSagas() {
        return sagas;
    }

}
