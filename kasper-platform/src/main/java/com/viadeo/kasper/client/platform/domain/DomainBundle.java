// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain;

import com.google.common.collect.Lists;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
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

    // ========================================================================

    public static class Builder {

        private final Domain domain;
        private final String domainName;

        private final List<CommandHandler> commandHandlers = Lists.newArrayList();
        private final List<QueryHandler> queryHandlers = Lists.newArrayList();
        private final List<EventListener> eventListeners = Lists.newArrayList();
        private final List<Repository> repositories = Lists.newArrayList();

        // --------------------------------------------------------------------

        public Builder(final Domain domain){
            this.domain = checkNotNull(domain);
            this.domainName = new DomainResolver().getLabel(domain.getClass());
        }

        // --------------------------------------------------------------------

        public Builder with(final CommandHandler commandHandler){
            commandHandlers.add(checkNotNull(commandHandler));
            return this;
        }

        public Builder with(final QueryHandler queryHandler){
            queryHandlers.add(checkNotNull(queryHandler));
            return this;
        }

        public Builder with(final EventListener eventListener){
            eventListeners.add(checkNotNull(eventListener));
            return this;
        }

        public Builder with(final Repository repository){
            repositories.add(checkNotNull(repository));
            return this;
        }

        public DomainBundle build(){
            return new DefaultDomainBundle(
                    commandHandlers,
                    queryHandlers,
                    repositories,
                    eventListeners,
                    domain,
                    domainName
            );
        }

    }
}
