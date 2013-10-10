// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc;

import com.viadeo.kasper.core.resolvers.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Resolvers {

    private DomainResolver domainResolver;
    private CommandResolver commandResolver;
    private EventListenerResolver eventListenerResolver;
    private QueryResolver queryResolver;
    private QueryServiceResolver queryServiceResolver;
    private RepositoryResolver repositoryResolver;
    private EntityResolver entityResolver;
    private ConceptResolver conceptResolver;
    private RelationResolver relationResolver;
    private EventResolver eventResolver;

    //-------------------------------------------------------------------------

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

    public QueryServiceResolver getQueryServiceResolver() {
        return queryServiceResolver;
    }

    public void setQueryServiceResolver(final QueryServiceResolver queryServiceResolver) {
        this.queryServiceResolver = checkNotNull(queryServiceResolver);
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

}
