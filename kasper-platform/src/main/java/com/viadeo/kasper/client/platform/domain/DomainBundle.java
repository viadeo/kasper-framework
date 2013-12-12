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
    void configure(BuilderContext context);

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
