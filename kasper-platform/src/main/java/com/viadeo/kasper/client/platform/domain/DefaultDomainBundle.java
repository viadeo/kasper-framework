package com.viadeo.kasper.client.platform.domain;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.NewPlatform;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

public class DefaultDomainBundle implements DomainBundle {

    private final List<CommandHandler> commandHandlers;
    private final List<QueryHandler> queryHandlers;
    private final List<IRepository> repositories;
    private final List<EventListener> eventListeners;
    private final Domain domain;
    private final String name;

    public DefaultDomainBundle(List<CommandHandler> commandHandlers, List<QueryHandler> queryHandlers, List<IRepository> repositories, List<EventListener> eventListeners, Domain domain, String name) {
        this.commandHandlers = commandHandlers;
        this.queryHandlers = queryHandlers;
        this.repositories = repositories;
        this.eventListeners = eventListeners;
        this.domain = domain;
        this.name = name;
    }

    public DefaultDomainBundle(Domain domain, String name) {
        this(Lists.<CommandHandler>newArrayList()
                , Lists.<QueryHandler>newArrayList()
                , Lists.<IRepository>newArrayList()
                , Lists.<EventListener>newArrayList()
                , domain
                , name
        );
    }

    @Override
    public void configure(NewPlatform.BuilderContext context) {

    }

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
    public List<IRepository> getRepositories() {
        return repositories;
    }
}
