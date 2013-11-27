package com.viadeo.kasper.client.platform.domain;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

public interface DomainBundle {
    void configure();

    Domain getDomain();

    List<CommandHandler> getCommandHandlers();

    List<QueryHandler> getQueryHandlers();

    List<EventListener> getEventListeners();

    List<IRepository> getRepositories();
}
