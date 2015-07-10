// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.saga.Saga;

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
    public void configure(final Platform.BuilderContext context) { }

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
