package com.viadeo.kasper.client.platform.domain;

import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

import static com.viadeo.kasper.client.platform.NewPlatform.BuilderContext;

public interface DomainBundle {
    void configure(BuilderContext context);

    String getName();

    Domain getDomain();

    List<CommandHandler> getCommandHandlers();

    List<QueryHandler> getQueryHandlers();

    List<EventListener> getEventListeners();

    List<Repository> getRepositories();
}
