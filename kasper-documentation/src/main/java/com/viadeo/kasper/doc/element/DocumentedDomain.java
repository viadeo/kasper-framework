// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.domain.descriptor.*;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.doc.element.DocumentedCommandHandler.DocumentedCommand;
import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQuery;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQueryResult;
import static com.viadeo.kasper.doc.element.DocumentedRepository.*;

public class DocumentedDomain extends AbstractElement {

    private final List<DocumentedQueryHandler> documentedQueryHandlers;
    private final List<DocumentedCommandHandler> documentedCommandHandlers;
    private final List<DocumentedEventListener> documentedEventListeners;
    private final List<DocumentedRepository> documentedRepositories;
    private final List<DocumentedQuery> queries;
    private final Map<Class, DocumentedQueryResult> queryResults;
    private final List<DocumentedConcept> concepts;
    private final List<DocumentedRelation> relations;
    private final List<DocumentedEvent> events;
    private final List<DocumentedCommand> commands;

    private String prefix;
    private Optional<DocumentedDomain> parent;
    private String owner;

    // ------------------------------------------------------------------------

    public DocumentedDomain(DomainDescriptor domainDescriptor) {
        super(DocumentedElementType.DOMAIN, domainDescriptor.getDomainClass());
        this.parent = Optional.absent();

        documentedQueryHandlers = Lists.newArrayList();
        documentedCommandHandlers = Lists.newArrayList();
        documentedEventListeners = Lists.newArrayList();
        documentedRepositories = Lists.newArrayList();
        queries = Lists.newArrayList();
        queryResults  = Maps.newHashMap();
        commands = Lists.newArrayList();
        events = Lists.newArrayList();
        relations = Lists.newArrayList();
        concepts = Lists.newArrayList();

        for (final QueryHandlerDescriptor descriptor : domainDescriptor.getQueryHandlerDescriptors()) {
            final DocumentedQueryHandler documentedQueryHandler = new DocumentedQueryHandler(this, descriptor);
            documentedQueryHandlers.add(documentedQueryHandler);
            queries.add(documentedQueryHandler.getQuery().getFullDocumentedElement());

            final LightDocumentedElement<DocumentedQueryResult> queryResult = documentedQueryHandler.getQueryResult();
            addQueryResult(queryResult.getFullDocumentedElement());
        }

        for (final CommandHandlerDescriptor descriptor : domainDescriptor.getCommandHandlerDescriptors()) {
            final DocumentedCommandHandler documentedCommandHandler = new DocumentedCommandHandler(this, descriptor);
            documentedCommandHandlers.add(documentedCommandHandler);
            commands.add(documentedCommandHandler.getCommand().getFullDocumentedElement());
        }

        final Map<Class, DocumentedEvent> events = Maps.newHashMap();
        final Map<Class, DocumentedConcept> concepts = Maps.newHashMap();

        for (final RepositoryDescriptor descriptor : domainDescriptor.getRepositoryDescriptors()) {
            final DocumentedRepository documentedRepository = new DocumentedRepository(this, descriptor);
            documentedRepositories.add(documentedRepository);

            final DocumentedAggregate aggregate = documentedRepository.getAggregate().getFullDocumentedElement();

            if (aggregate instanceof DocumentedRelation) {
                relations.add((DocumentedRelation) aggregate);
            } else if (aggregate instanceof DocumentedConcept) {
                concepts.put(aggregate.getReferenceClass(), (DocumentedConcept) aggregate);
            }

            for(LightDocumentedElement<DocumentedEvent> lightDocumentedEvent : aggregate.getSourceEvents()){
                DocumentedEvent documentedEvent = lightDocumentedEvent.getFullDocumentedElement();
                events.put(documentedEvent.getReferenceClass(), documentedEvent);
            }

        }

        this.concepts.addAll(concepts.values());

        for (final EventListenerDescriptor descriptor : domainDescriptor.getEventListenerDescriptors()) {
            final DocumentedEventListener documentedEventListener = new DocumentedEventListener(this, descriptor);
            documentedEventListeners.add(documentedEventListener);

            final DocumentedEvent documentedEvent = documentedEventListener.getEvent().getFullDocumentedElement();
            events.put(documentedEvent.getReferenceClass(), documentedEvent);
        }

        this.events.addAll(events.values());
    }

    // ------------------------------------------------------------------------

    @Override
    public String getURL() {
        return String.format("/%s/%s", getType(), getName());
    }

    @Override
    public LightDocumentedElement<DocumentedDomain> getLightDocumentedElement() {
        return new LightDocumentedElement<>(this);
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        visitor.visit(this);

        final List<AbstractElement> documentedElements = Lists.newArrayList();
        documentedElements.addAll(documentedQueryHandlers);
        documentedElements.addAll(documentedCommandHandlers);
        documentedElements.addAll(documentedEventListeners);
        documentedElements.addAll(documentedRepositories);

        for (final AbstractElement documentedElement : documentedElements) {
            documentedElement.accept(visitor);
        }
    }

    // ------------------------------------------------------------------------

    public Collection<DocumentedQueryHandler> getQueryHandlers() {
        return documentedQueryHandlers;
    }

    public Collection<DocumentedCommandHandler> getCommandHandlers() {
        return documentedCommandHandlers;
    }

    public Collection<DocumentedEventListener> getEventListeners() {
        return documentedEventListeners;
    }

    public Collection<DocumentedRepository> getRepositories() {
        return documentedRepositories;
    }

    public Collection<DocumentedQuery> getQueries() {
        return queries;
    }

    public Collection<DocumentedQueryResult> getQueryResults() {
        return queryResults.values();
    }

    @JsonIgnore
    public Optional<DocumentedQueryResult> getQueryResult(Class queryResultClass) {
        return Optional.fromNullable(queryResults.get(queryResultClass));
    }

    public void addQueryResult(DocumentedQueryResult queryResult) {
        checkNotNull(queryResult);
        queryResults.put(queryResult.getReferenceClass(), queryResult);
    }

    public Collection<DocumentedCommand> getCommands() {
        return commands;
    }

    public Collection<DocumentedEvent> getEvents() {
        return events;
    }

    public Collection<DocumentedConcept> getConcepts() {
        return concepts;
    }

    public Collection<DocumentedRelation> getRelations() {
        return relations;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getOwner() {
        return owner;
    }

    @JsonIgnore
    public Optional<DocumentedDomain> getParent(){
       return parent;
    }

    // ------------------------------------------------------------------------

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public void setParent(final Optional<DocumentedDomain> parent) {
        this.parent = parent;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
