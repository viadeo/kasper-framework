package com.viadeo.kasper.client.platform.domain;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

public class DefaultDomainBundle implements DomainBundle {

    protected final List<CommandHandler> commandHandlers;
    protected final List<QueryHandler> queryHandlers;
    protected final List<Repository> repositories;
    protected final List<EventListener> eventListeners;
    protected final Domain domain;
    protected final String name;

    public DefaultDomainBundle(Domain domain) {
        this(domain, new DomainResolver().getLabel(domain.getClass()));
    }

    public DefaultDomainBundle(Domain domain, String name) {
        this(Lists.<CommandHandler>newArrayList()
                , Lists.<QueryHandler>newArrayList()
                , Lists.<Repository>newArrayList()
                , Lists.<EventListener>newArrayList()
                , domain
                , name
        );
    }

    public DefaultDomainBundle(List<CommandHandler> commandHandlers, List<QueryHandler> queryHandlers, List<Repository> repositories, List<EventListener> eventListeners, Domain domain, String name) {
        this.commandHandlers = commandHandlers;
        this.queryHandlers = queryHandlers;
        this.repositories = repositories;
        this.eventListeners = eventListeners;
        this.domain = domain;
        this.name = name;
    }

    @Override
    public void configure(Platform.BuilderContext context) { }

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
}
