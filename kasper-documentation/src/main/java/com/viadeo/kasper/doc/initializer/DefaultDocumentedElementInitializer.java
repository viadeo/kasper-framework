// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.initializer;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.doc.element.*;

import java.util.List;
import java.util.Map;

import static com.viadeo.kasper.doc.element.DocumentedCommandHandler.DocumentedCommand;
import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQuery;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.DocumentedQueryResult;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedConcept;
import static com.viadeo.kasper.doc.element.DocumentedRepository.DocumentedRelation;

@SuppressWarnings("unchecked")
public class DefaultDocumentedElementInitializer implements DocumentedElementVisitor {

    private final DocumentedPlatform documentedPlatform;
    private final Map<Class, DocumentedDomain> documentedDomainByDeclaredEvent;

    // ------------------------------------------------------------------------

    public DefaultDocumentedElementInitializer(final DocumentedPlatform documentedPlatform) {
        this.documentedPlatform = documentedPlatform;
        this.documentedDomainByDeclaredEvent = Maps.newHashMap();

        for (DocumentedDomain domain : documentedPlatform.getDomains()) {
            for (DocumentedEvent event : domain.getDeclaredEvents()) {
                documentedDomainByDeclaredEvent.put(event.getReferenceClass(), domain);
            }
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void visit(final DocumentedDomain domain) {
        final DomainResolver resolver = new DomainResolver();
        final Class<? extends Domain> referenceClass = domain.getReferenceClass();

        if (domain == DocumentedDomain.UNKNOWN_DOMAIN) {
            domain.setLabel("unknown");
        } else {
            domain.setPrefix(resolver.getPrefix(referenceClass));
            domain.setLabel(resolver.getDomainLabel(referenceClass));
            domain.setDescription(resolver.getDescription(referenceClass));
            domain.setOwner(resolver.getDomainOwner(referenceClass));

            final Class parentClass = domain.getReferenceClass().getSuperclass();
            final Optional<DocumentedDomain> parentDomain = documentedPlatform.getDomain(parentClass);

            if (parentDomain.isPresent()) {
                domain.setParent(parentDomain);
            }
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
                final DocumentedQueryResult element;

                if(documentedQuery.isPresent()){
                    element = documentedQuery.get();
                } else {
                    element = new DocumentedQueryResult(documentedDomain, null, elementClass);
                    visit(element);
                    documentedDomain.addQueryResult(element);
                }

                queryResult.setElement(element);
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

        DocumentedDomain referencedDomain = documentedDomainByDeclaredEvent.get(referenceClass);
        if (referencedDomain != null) {
            event.setDomain(referencedDomain);
        }
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
        final Class<? extends Repository> referenceClass = repository.getReferenceClass();

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

    @Override
    public void visit(final DocumentedSaga saga) {
        final SagaResolver resolver = new SagaResolver();
        final Class<? extends Saga> referenceClass = saga.getReferenceClass();

        saga.setLabel(resolver.getLabel(referenceClass));
        saga.setDescription(resolver.getDescription(referenceClass));
    }
}
