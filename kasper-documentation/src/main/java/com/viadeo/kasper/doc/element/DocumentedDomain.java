package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.domain.descriptor.*;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.viadeo.kasper.doc.element.DocumentedCommandHandler.DocumentedCommand;
import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQuery;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQueryResult;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedAggregate;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedConcept;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedRelation;

public class DocumentedDomain extends AbstractElement {

    private final List<DocumentedQueryHandler> documentedQueryHandlers;
    private final List<DocumentedCommandHandler> documentedCommandHandlers;
    private final List<DocumentedEventListener> documentedEventListeners;
    private final List<DocumentedRepository> documentedRepositories;
    private final List<DocumentedQuery> queries;
    private final List<DocumentedQueryResult> queryResults;
    private final List<DocumentedConcept> concepts;
    private final List<DocumentedRelation> relations;
    private final List<DocumentedEvent> events;
    private final List<DocumentedCommand> commands;

    private String prefix;

    public DocumentedDomain(DomainDescriptor domainDescriptor) {
        super(DocumentedElementType.DOMAIN, domainDescriptor.getDomainClass());

        documentedQueryHandlers = Lists.newArrayList();
        documentedCommandHandlers = Lists.newArrayList();
        documentedEventListeners = Lists.newArrayList();
        documentedRepositories = Lists.newArrayList();
        queries = Lists.newArrayList();
        queryResults = Lists.newArrayList();
        commands = Lists.newArrayList();
        events = Lists.newArrayList();
        relations = Lists.newArrayList();
        concepts = Lists.newArrayList();

        for (QueryHandlerDescriptor descriptor : domainDescriptor.getQueryHandlerDescriptors()) {
            DocumentedQueryHandler documentedQueryHandler = new DocumentedQueryHandler(this, descriptor);
            documentedQueryHandlers.add(documentedQueryHandler);
            queries.add(documentedQueryHandler.getQuery().getFullDocumentedElement());
            queryResults.add(documentedQueryHandler.getQueryResult().getFullDocumentedElement());
        }

        for (CommandHandlerDescriptor descriptor : domainDescriptor.getCommandHandlerDescriptors()) {
            DocumentedCommandHandler documentedCommandHandler = new DocumentedCommandHandler(this, descriptor);
            documentedCommandHandlers.add(documentedCommandHandler);
            commands.add(documentedCommandHandler.getCommand().getFullDocumentedElement());
        }

        Map<Class, DocumentedEvent> events = Maps.newHashMap();
        Map<Class, DocumentedConcept> concepts = Maps.newHashMap();

        for (RepositoryDescriptor descriptor : domainDescriptor.getRepositoryDescriptors()) {
            DocumentedRepository documentedRepository = new DocumentedRepository(this, descriptor);
            documentedRepositories.add(documentedRepository);

            DocumentedAggregate aggregate = documentedRepository.getAggregate().getFullDocumentedElement();

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

        for (EventListenerDescriptor descriptor : domainDescriptor.getEventListenerDescriptors()) {
            DocumentedEventListener documentedEventListener = new DocumentedEventListener(this, descriptor);
            documentedEventListeners.add(documentedEventListener);

            DocumentedEvent documentedEvent = documentedEventListener.getEvent().getFullDocumentedElement();
            events.put(documentedEvent.getReferenceClass(), documentedEvent);
        }
        this.events.addAll(events.values());
    }

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
        return queryResults;
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

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getURL() {
        return String.format("/%s/%s", getType(), getName());
    }

    @Override
    public LightDocumentedElement<DocumentedDomain> getLightDocumentedElement() {
        return new LightDocumentedElement<>(this);
    }

    @Override
    public void accept(DocumentedElementVisitor visitor) {
        List<AbstractElement> documentedElements = Lists.newArrayList();
        documentedElements.addAll(documentedQueryHandlers);
        documentedElements.addAll(documentedCommandHandlers);
        documentedElements.addAll(documentedEventListeners);
        documentedElements.addAll(documentedRepositories);

        for (AbstractElement documentedElement : documentedElements) {
            documentedElement.accept(visitor);
        }

        visitor.visit(this);
    }
}
