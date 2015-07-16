// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.aggregate.ddd.Entity;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.saga.Saga;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tool resolver for domain components
 */
public class ResolverFactory {

    private static ConcurrentMap<Class, Optional<Resolver>> cache = Maps.newConcurrentMap();

    private DomainResolver domainResolver;
    private CommandResolver commandResolver;
    private CommandHandlerResolver commandHandlerResolver;
    private EventListenerResolver eventListenerResolver;
    private QueryResolver queryResolver;
    private QueryResultResolver queryResultResolver;
    private QueryHandlerResolver queryHandlerResolver;
    private RepositoryResolver repositoryResolver;
    private EntityResolver entityResolver;
    private ConceptResolver conceptResolver;
    private RelationResolver relationResolver;
    private EventResolver eventResolver;
    private SagaResolver sagaResolver;

    private Map<Class, Resolver> resolvers;

    // ------------------------------------------------------------------------

    private void initResolvers() {
        if (null == resolvers) {
            resolvers = new LinkedHashMap<Class, Resolver>() {
                {
                    put(Domain.class, domainResolver);
                    put(Command.class, commandResolver);
                    put(CommandHandler.class, commandHandlerResolver);
                    put(EventListener.class, eventListenerResolver);
                    put(Query.class, queryResolver);
                    put(QueryResult.class, queryResultResolver);
                    put(QueryHandler.class, queryHandlerResolver);
                    put(Repository.class, repositoryResolver);
                    put(Event.class, eventResolver);
                    put(Saga.class, sagaResolver);

                    /* Order is important here (Concept/Relation before Entity) */
                    put(Concept.class, conceptResolver);
                    put(Relation.class, relationResolver);
                    put(Entity.class, entityResolver);
                }
            };
        }
    }

    // ------------------------------------------------------------------------

    public Optional<Resolver> getResolverFromClass(final Class clazz) {

        if (checkNotNull(clazz).equals(EventListener.class)) {
            return Optional.absent();
        }

        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }

        initResolvers();

        Resolver resolver = null;

        for (final Map.Entry<Class, Resolver> resolverEntry : resolvers.entrySet()) {
            if (resolverEntry.getKey().isAssignableFrom(clazz)) {
                resolver = resolverEntry.getValue();
                break;
            }
        }

        final Optional<Resolver> optResolver = Optional.fromNullable(resolver);
        cache.put(clazz, optResolver);
        return optResolver;
    }

    // ------------------------------------------------------------------------

    public DomainResolver getDomainResolver() {
        return domainResolver;
    }

    public void setDomainResolver(final DomainResolver domainResolver) {
        this.domainResolver = checkNotNull(domainResolver);
    }

    //-------------------------------------------------------------------------

    public CommandResolver getCommandResolver() {
        return commandResolver;
    }

    public void setCommandResolver(final CommandResolver commandResolver) {
        this.commandResolver = checkNotNull(commandResolver);
    }

    //-------------------------------------------------------------------------

    public EventListenerResolver getEventListenerResolver() {
        return eventListenerResolver;
    }

    public void setEventListenerResolver(final EventListenerResolver eventListenerResolver) {
        this.eventListenerResolver = checkNotNull(eventListenerResolver);
    }

    //-------------------------------------------------------------------------

    public QueryResolver getQueryResolver() {
        return queryResolver;
    }

    public void setQueryResolver(final QueryResolver queryResolver) {
        this.queryResolver = checkNotNull(queryResolver);
    }

    //-------------------------------------------------------------------------

    public QueryResultResolver getQueryResultResolver() {
        return queryResultResolver;
    }

    public void setQueryResultResolver(final QueryResultResolver queryResultResolver) {
        this.queryResultResolver = checkNotNull(queryResultResolver);
    }

    //-------------------------------------------------------------------------

    public QueryHandlerResolver getQueryHandlerResolver() {
        return queryHandlerResolver;
    }

    public void setQueryHandlerResolver(final QueryHandlerResolver queryHandlerResolver) {
        this.queryHandlerResolver = checkNotNull(queryHandlerResolver);
    }

    //-------------------------------------------------------------------------

    public RepositoryResolver getRepositoryResolver() {
        return repositoryResolver;
    }

    public void setRepositoryResolver(final RepositoryResolver repositoryResolver) {
        this.repositoryResolver = checkNotNull(repositoryResolver);
    }

    //-------------------------------------------------------------------------

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

    //-------------------------------------------------------------------------

    public ConceptResolver getConceptResolver() {
        return conceptResolver;
    }

    public void setConceptResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
    }

    //-------------------------------------------------------------------------

    public RelationResolver getRelationResolver() {
        return relationResolver;
    }

    public void setRelationResolver(final RelationResolver relationResolver) {
        this.relationResolver = checkNotNull(relationResolver);
    }

    //-------------------------------------------------------------------------

    public EventResolver getEventResolver() {
        return eventResolver;
    }

    public void setEventResolver(final EventResolver eventResolver) {
        this.eventResolver = checkNotNull(eventResolver);
    }

    // ------------------------------------------------------------------------

    public CommandHandlerResolver getCommandHandlerResolver() {
        return commandHandlerResolver;
    }

    public void setCommandHandlerResolver(final CommandHandlerResolver commandHandlerResolver) {
        this.commandHandlerResolver = checkNotNull(commandHandlerResolver);
    }

    // ------------------------------------------------------------------------

    public SagaResolver getSagaResolver() {
        return sagaResolver;
    }

    public void setSagaResolver(final SagaResolver sagaResolver) {
        this.sagaResolver = checkNotNull(sagaResolver);
    }

    // ------------------------------------------------------------------------

    public void clearCaches() {
        cache.clear();
        for (final Resolver resolver : resolvers.values()) {
            if (null != resolver) {
                resolver.clearCache();
            }
        }
    }

}
