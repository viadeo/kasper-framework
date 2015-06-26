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
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.doc.nodes.DocumentedBean;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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
    private final List<DocumentedSaga> documentedSagas;
    private final List<DocumentedQuery> queries;
    private final Map<Class, DocumentedQueryResult> queryResults;
    private final List<DocumentedConcept> concepts;
    private final List<DocumentedConcept> avatarConcepts;
    private final List<DocumentedRelation> relations;
    private final List<DocumentedEvent> events;
    private final List<DocumentedEvent> declaredEvents;
    private final List<DocumentedEvent> referencedEvents;
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
        declaredEvents = Lists.newArrayList();
        referencedEvents = Lists.newArrayList();
        relations = Lists.newArrayList();
        concepts = Lists.newArrayList();
        avatarConcepts = Lists.newArrayList();
        documentedSagas = Lists.newArrayList();

        // QUERY
        for (final QueryHandlerDescriptor descriptor : domainDescriptor.getQueryHandlerDescriptors()) {
            final DocumentedQueryHandler documentedQueryHandler = new DocumentedQueryHandler(this, descriptor);
            documentedQueryHandlers.add(documentedQueryHandler);
            queries.add(documentedQueryHandler.getQuery().getFullDocumentedElement());

            final LightDocumentedElement<DocumentedQueryResult> queryResult = documentedQueryHandler.getQueryResult();
            addQueryResult(queryResult.getFullDocumentedElement());
        }

        // orphan query results; ie without use directly by an handler
        queryResults.putAll(findOrphanQueryResult(this, queryResults));

        // COMMAND
        for (final CommandHandlerDescriptor descriptor : domainDescriptor.getCommandHandlerDescriptors()) {
            final DocumentedCommandHandler documentedCommandHandler = new DocumentedCommandHandler(this, descriptor);
            documentedCommandHandlers.add(documentedCommandHandler);
            commands.add(documentedCommandHandler.getCommand().getFullDocumentedElement());
        }


        final Map<Class, DocumentedEvent> declaredEvents = Maps.newHashMap();
        final Map<Class, DocumentedEvent> referencedEvents = Maps.newHashMap();
        final Map<Class, DocumentedConcept> concepts = Maps.newHashMap();

        // EVENT
        for (final Class<? extends Event> eventClass : domainDescriptor.getEventClasses()) {
            DocumentedEvent documentedEvent = new DocumentedEvent(this, null, eventClass);
            declaredEvents.put(eventClass, documentedEvent);
        }

        // REPOSITORY
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

                if ( ! declaredEvents.containsKey(documentedEvent.getReferenceClass())) {
                    referencedEvents.put(documentedEvent.getReferenceClass(), documentedEvent);
                }
            }
        }

        // RELATION
        for (final DocumentedRelation documentedRelation:relations) {
            final Class sourceReferenceClass = documentedRelation.getSourceConcept().getReferenceClass();
            if ( ! concepts.containsKey(sourceReferenceClass)) {
                final DocumentedConcept documentedConcept = new DocumentedConcept(this, sourceReferenceClass, null);
                concepts.put(sourceReferenceClass, documentedConcept);
                avatarConcepts.add(documentedConcept);
            }

            final Class targetReferenceClass = documentedRelation.getTargetConcept().getReferenceClass();
            if ( ! concepts.containsKey(targetReferenceClass)) {
                final DocumentedConcept documentedConcept = new DocumentedConcept(this, targetReferenceClass, null);
                concepts.put(targetReferenceClass, documentedConcept);
                avatarConcepts.add(documentedConcept);
            }
        }

        this.concepts.addAll(concepts.values());

        // LISTENER
        for (final EventListenerDescriptor descriptor : domainDescriptor.getEventListenerDescriptors()) {
            final DocumentedEventListener documentedEventListener = new DocumentedEventListener(this, descriptor);
            documentedEventListeners.add(documentedEventListener);

            final DocumentedEvent documentedEvent = documentedEventListener.getEvent().getFullDocumentedElement();

            if ( ! declaredEvents.containsKey(documentedEvent.getReferenceClass())) {
                referencedEvents.put(documentedEvent.getReferenceClass(), documentedEvent);
            }
        }

        // SAGA
        for (final SagaDescriptor descriptor : domainDescriptor.getSagaDescriptors()) {
            documentedSagas.add(new DocumentedSaga(this, descriptor));
             // TODO
        }

        final Map<Class, DocumentedEvent> events = Maps.newHashMap();
        events.putAll(referencedEvents);
        events.putAll(declaredEvents);

        this.events.addAll(events.values());
        this.declaredEvents.addAll(declaredEvents.values());
        this.referencedEvents.addAll(referencedEvents.values());

        final Comparator<AbstractElement> abstractElementComparator = new Comparator<AbstractElement>() {
            @Override
            public int compare(AbstractElement o1, AbstractElement o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        };

        Collections.sort(commands, abstractElementComparator);
        Collections.sort(queries, abstractElementComparator);
        Collections.sort(this.events, abstractElementComparator);
        Collections.sort(this.declaredEvents, abstractElementComparator);
        Collections.sort(this.referencedEvents, abstractElementComparator);
        Collections.sort(documentedSagas, abstractElementComparator);
    }

    private Map<Class, DocumentedQueryHandler.DocumentedQueryResult> findOrphanQueryResult(
            final DocumentedDomain domain,
            final Map<Class, DocumentedQueryHandler.DocumentedQueryResult> resultMap
    ) {
        final Map<Class, DocumentedQueryHandler.DocumentedQueryResult> results = Maps.newHashMap();
        results.putAll(resultMap);

        for (final DocumentedQueryHandler.DocumentedQueryResult documentedQueryResult : resultMap.values()) {
            final Map<Class, DocumentedQueryHandler.DocumentedQueryResult> orphanQueryResult = findOrphanQueryResult(
                    domain,
                    results.keySet(),
                    documentedQueryResult
            );

            if( ! orphanQueryResult.isEmpty()) {
                results.putAll(orphanQueryResult);
            }
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    private Map<Class, DocumentedQueryHandler.DocumentedQueryResult> findOrphanQueryResult(
            final DocumentedDomain domain,
            final Set<Class> referencedQueryResult,
            final DocumentedQueryHandler.DocumentedQueryResult documentedQueryResult
    ) {
        final Map<Class, DocumentedQueryHandler.DocumentedQueryResult> results = Maps.newHashMap();
        final Class referenceClazz = documentedQueryResult.getReferenceClass();

        if ( QueryResult.class.isAssignableFrom(referenceClazz) && ! referencedQueryResult.contains(referenceClazz) ) {
            final DocumentedQueryHandler.DocumentedQueryResult value = new DocumentedQueryHandler.DocumentedQueryResult(domain, null, referenceClazz);
            results.put(value.getReferenceClass(), value);
            results.putAll(findOrphanQueryResult(domain, results.keySet(), value));

        } else if(CollectionQueryResult.class.isAssignableFrom(referenceClazz)) {
            final ParameterizedType genericSuperclass = (ParameterizedType) referenceClazz.getGenericSuperclass();
            final Type[] parameters = genericSuperclass.getActualTypeArguments();
            final Class GenericClass = (Class) parameters[0];

            if( ! referencedQueryResult.contains(referenceClazz)) {
                final DocumentedQueryHandler.DocumentedQueryResult value = new DocumentedQueryHandler.DocumentedQueryResult(domain, null, referenceClazz);
                results.put(value.getReferenceClass(), value);
                results.putAll(findOrphanQueryResult(domain, results.keySet(), value));
            }

            if( ! referencedQueryResult.contains(GenericClass)) {
                final DocumentedQueryHandler.DocumentedQueryResult value = new DocumentedQueryHandler.DocumentedQueryResult(domain, null, GenericClass);
                results.put(GenericClass, value);
                results.putAll(findOrphanQueryResult(domain, results.keySet(), value));
            }
        } else {
            final List<Field> fields = Lists.newArrayList();
            DocumentedBean.getAllFields(fields, documentedQueryResult.getReferenceClass());

            for (final Field field : fields) {
                final Class<?> fieldType = field.getType();

                if ( QueryResult.class.isAssignableFrom(fieldType) && ! referencedQueryResult.contains(fieldType) ) {
                    final DocumentedQueryHandler.DocumentedQueryResult value = new DocumentedQueryHandler.DocumentedQueryResult(domain, null, fieldType);
                    results.put(value.getReferenceClass(), value);
                    results.putAll(findOrphanQueryResult(domain, results.keySet(), value));

                } else if(CollectionQueryResult.class.isAssignableFrom(fieldType) && ! referencedQueryResult.contains(fieldType) ) {
                    final DocumentedQueryHandler.DocumentedQueryResult value = new DocumentedQueryHandler.DocumentedQueryResult(domain, null, fieldType);
                    results.put(value.getReferenceClass(), value);
                    results.putAll(findOrphanQueryResult(domain, results.keySet(), value));

                } else if( Collection.class.isAssignableFrom(fieldType) ){
                    final Optional<Class> optType = (Optional<Class>)
                            ReflectionGenericsResolver.getParameterTypeFromClass(
                                    field, referenceClazz, Collection.class, 0);

                    if ( optType.isPresent() ) {
                        final Class genericFieldType = optType.get();
                        if (QueryResult.class.isAssignableFrom(genericFieldType) && ! referencedQueryResult.contains(genericFieldType)) {
                            final DocumentedQueryHandler.DocumentedQueryResult value = new DocumentedQueryHandler.DocumentedQueryResult(domain, null, genericFieldType);
                            results.put(value.getReferenceClass(), value);
                            results.putAll(findOrphanQueryResult(domain, results.keySet(), value));
                        }
                    }
                }
            }
        }

        return results;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getURL() {
        return String.format("/%s/%s", getType(), getLabel());
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
        documentedElements.addAll(avatarConcepts);
        documentedElements.addAll(documentedSagas);

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

    public List<DocumentedEvent> getDeclaredEvents() {
        return declaredEvents;
    }

    public List<DocumentedEvent> getReferencedEvents() {
        return referencedEvents;
    }

    public Collection<DocumentedConcept> getConcepts() {
        return concepts;
    }

    public Collection<DocumentedRelation> getRelations() {
        return relations;
    }

    public List<DocumentedSaga> getSagas() {
        return documentedSagas;
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

    public void setOwner(final String owner) {
        this.owner = owner;
    }

}
