// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.initializer;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.doc.element.*;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;

import java.util.List;

import static com.viadeo.kasper.doc.element.DocumentedCommandHandler.DocumentedCommand;
import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQuery;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQueryResult;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedConcept;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedRelation;

public class DefaultDocumentedElementInitializer implements DocumentedElementVisitor {

    private final DocumentedPlatform documentedPlatform;

    // ------------------------------------------------------------------------

    public DefaultDocumentedElementInitializer(final DocumentedPlatform documentedPlatform) {
        this.documentedPlatform = documentedPlatform;
    }

    // ------------------------------------------------------------------------

    @Override
    public void visit(final DocumentedDomain domain) {
        final DomainResolver resolver = new DomainResolver();
        final Class<? extends Domain> referenceClass = domain.getReferenceClass();

        domain.setPrefix(resolver.getPrefix(referenceClass));
        domain.setLabel(resolver.getDomainLabel(referenceClass));
        domain.setDescription(resolver.getDescription(referenceClass));

        final Class parentClass = domain.getReferenceClass().getSuperclass();
        final Optional<DocumentedDomain> parentDomain = documentedPlatform.getDomain(parentClass);

        if (parentDomain.isPresent()) {
            domain.setParent(parentDomain);
        }
    }

    @Override
    public void visit(final DocumentedCommand command) {
        final CommandResolver resolver = new CommandResolver();
        final Class<? extends Command> referenceClass = command.getReferenceClass();

        command.setLabel(resolver.getLabel(referenceClass));
        command.setDescription(resolver.getDescription(referenceClass));
    }

    @Override
    public void visit(final DocumentedCommandHandler commandHandler) {
        final CommandHandlerResolver resolver = new CommandHandlerResolver();
        final Class<? extends CommandHandler> referenceClass = commandHandler.getReferenceClass();

        commandHandler.setLabel(resolver.getLabel(referenceClass));
        commandHandler.setDescription(resolver.getDescription(referenceClass));
    }

    @Override
    public void visit(final DocumentedQuery query) {
        final QueryResolver resolver = new QueryResolver();
        final Class<? extends Query> referenceClass = query.getReferenceClass();

        query.setLabel(resolver.getLabel(referenceClass));
        query.setDescription(resolver.getDescription(referenceClass));
    }

    @Override
    public void visit(final DocumentedQueryResult queryResult) {
        final QueryResultResolver resolver = new QueryResultResolver();
        final Class<? extends QueryResult> referenceClass = queryResult.getReferenceClass();

        queryResult.setLabel(resolver.getLabel(referenceClass));
        queryResult.setDescription(resolver.getDescription(referenceClass));

        if(CollectionQueryResult.class.isAssignableFrom(referenceClass)) {
            final DocumentedDomain documentedDomain = queryResult.getDomain().getFullDocumentedElement();
            final Class<? extends QueryResult> elementClass = resolver.getElementClass(queryResult.getReferenceClass());

            if(null != elementClass) {
                final Optional<DocumentedQueryResult> documentedQuery = documentedDomain.getQueryResult(elementClass);

                if(documentedQuery.isPresent()){
                    queryResult.setElement(documentedQuery.get());
                }
            }
        }
    }

    @Override
    public void visit(final DocumentedQueryHandler queryHandler) {
        final QueryHandlerResolver resolver = new QueryHandlerResolver();
        final Class<? extends QueryHandler> referenceClass = queryHandler.getReferenceClass();

        queryHandler.setLabel(resolver.getLabel(referenceClass));
        queryHandler.setDescription(resolver.getDescription(referenceClass));
    }

    @Override
    public void visit(final DocumentedEvent event) {
        final EventResolver resolver = new EventResolver();
        final Class<? extends Event> referenceClass = event.getReferenceClass();

        event.setLabel(resolver.getLabel(referenceClass));
        event.setDescription(resolver.getDescription(referenceClass));
        event.setAction(resolver.getAction(referenceClass));
    }

    @Override
    public void visit(DocumentedEventListener eventListener) {
        EventListenerResolver resolver = new EventListenerResolver();
        Class<? extends EventListener> referenceClass = eventListener.getReferenceClass();

        eventListener.setLabel(resolver.getLabel(referenceClass));
        eventListener.setDescription(resolver.getDescription(referenceClass));
    }

    @Override
    public void visit(final DocumentedRepository repository) {
        final RepositoryResolver resolver = new RepositoryResolver(new EntityResolver(new ConceptResolver(), new RelationResolver()));
        final Class<? extends IRepository> referenceClass = repository.getReferenceClass();

        repository.setLabel(resolver.getLabel(referenceClass));
        repository.setDescription(resolver.getDescription(referenceClass));

    }

    @Override
    public void visit(final DocumentedConcept concept) {
        final ConceptResolver resolver = new ConceptResolver();
        final Class<? extends Concept> referenceClass = concept.getReferenceClass();

        concept.setLabel(resolver.getLabel(referenceClass));
        concept.setDescription(resolver.getDescription(referenceClass));

        final DocumentedDomain documentedDomain = concept.getDomain().getFullDocumentedElement();

        final List<DocumentedRelation> relations = Lists.newArrayList();
        relations.addAll(documentedDomain.getRelations());

        final List<DocumentedConcept> concepts = Lists.newArrayList();
        concepts.addAll(documentedDomain.getConcepts());

        if (documentedDomain.getParent().isPresent()) {
            relations.addAll(documentedDomain.getParent().get().getRelations());
            concepts.addAll(documentedDomain.getParent().get().getConcepts());
        }

        for (final DocumentedRelation relation : relations) {
            if (concept.getReferenceClass().equals(relation.getSourceConcept().getReferenceClass())) {
                concept.addSourceRelation(relation);
            }
            if (concept.getReferenceClass().equals(relation.getTargetConcept().getReferenceClass())) {
                concept.addTargetRelation(relation);
            }
        }

        final EntityResolver entityResolver = new EntityResolver(resolver, new RelationResolver());
        final List<Class<? extends Concept>> componentConceptClasses = entityResolver.getComponentConcepts(concept.getReferenceClass());

        for(final DocumentedConcept documentedConcept : concepts){
            if (componentConceptClasses.contains(documentedConcept.getReferenceClass())) {
                concept.addComponentConcept(documentedConcept);
            }
        }
    }

    @Override
    public void visit(final DocumentedRelation relation) {
        final RelationResolver resolver = new RelationResolver(new ConceptResolver());
        final Class<? extends Relation> referenceClass = relation.getReferenceClass();

        relation.setBidirectional(resolver.isBidirectional(referenceClass));
        relation.setLabel(resolver.getLabel(referenceClass));
        relation.setDescription(resolver.getDescription(referenceClass));
        relation.setVerb(resolver.getVerb(referenceClass));
    }

}
