package com.viadeo.kasper.client.platform.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

import static com.viadeo.kasper.client.platform.Platform.BuilderContext;

public interface DomainBundle {

    void configure(BuilderContext context);

    String getName();

    Domain getDomain();

    List<CommandHandler> getCommandHandlers();

    List<QueryHandler> getQueryHandlers();

    List<EventListener> getEventListeners();

    List<Repository> getRepositories();


    public static class Builder {

        private final Domain domain;
        private final String domainName;
        private final List<CommandHandler> commandHandlers;
        private final List<QueryHandler> queryHandlers;
        private final List<EventListener> eventListeners;
        private final List<Repository> repositories;

        public Builder(Domain domain){
            this.domain = Preconditions.checkNotNull(domain);
            this.domainName = new DomainResolver().getLabel(domain.getClass());
            this.commandHandlers = Lists.newArrayList();
            this.queryHandlers = Lists.newArrayList();
            this.eventListeners = Lists.newArrayList();
            this.repositories = Lists.newArrayList();
        }

        public Builder with(CommandHandler commandHandler){
            commandHandlers.add(Preconditions.checkNotNull(commandHandler));
            return this;
        }

        public Builder with(QueryHandler queryHandler){
            queryHandlers.add(Preconditions.checkNotNull(queryHandler));
            return this;
        }

        public Builder with(EventListener eventListener){
            eventListeners.add(Preconditions.checkNotNull(eventListener));
            return this;
        }

        public Builder with(Repository repository){
            repositories.add(Preconditions.checkNotNull(repository));
            return this;
        }

        public DomainBundle build(){
            return new DefaultDomainBundle(
                    commandHandlers
                    , queryHandlers
                    , repositories
                    , eventListeners
                    , domain
                    , domainName
            );
        }
    }
}
