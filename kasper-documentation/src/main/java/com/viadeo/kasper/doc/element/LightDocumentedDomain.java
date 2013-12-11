// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.Collection;

public class LightDocumentedDomain extends LightDocumentedElement<DocumentedDomain> {

    private final Collection<LightDocumentedElement> commands;
    private final Collection<LightDocumentedElement> commandHandlers;
    private final Collection<LightDocumentedElement> queries;
    private final Collection<LightDocumentedElement> queryResults;
    private final Collection<LightDocumentedElement> queryHandlers;
    private final Collection<LightDocumentedElement> events;
    private final Collection<LightDocumentedElement> eventListeners;
    private final Collection<LightDocumentedElement> concepts;
    private final Collection<LightDocumentedElement> relations;
    private final Collection<LightDocumentedElement> repositories;

    private static final Function<AbstractElement, LightDocumentedElement> LIGHTER = new Function<AbstractElement, LightDocumentedElement>() {
        @Override
        public LightDocumentedElement apply(AbstractElement input) {
            return input.getLightDocumentedElement();
        }
    };

    // ------------------------------------------------------------------------

    public LightDocumentedDomain(final DocumentedDomain documentedDomain) {
        super(documentedDomain);
        this.commands = Collections2.transform(documentedDomain.getCommands(), LIGHTER);
        this.commandHandlers = Collections2.transform(documentedDomain.getCommandHandlers(), LIGHTER);
        this.queries = Collections2.transform(documentedDomain.getQueries(), LIGHTER);
        this.queryResults = Collections2.transform(documentedDomain.getQueryResults(), LIGHTER);
        this.queryHandlers = Collections2.transform(documentedDomain.getQueryHandlers(), LIGHTER);
        this.events = Collections2.transform(documentedDomain.getEvents(), LIGHTER);
        this.eventListeners = Collections2.transform(documentedDomain.getEventListeners(), LIGHTER);
        this.concepts = Collections2.transform(documentedDomain.getConcepts(), LIGHTER);
        this.relations = Collections2.transform(documentedDomain.getRelations(), LIGHTER);
        this.repositories = Collections2.transform(documentedDomain.getRepositories(), LIGHTER);
    }

    // ------------------------------------------------------------------------

    public Collection<LightDocumentedElement> getQueryHandlers() {
        return queryHandlers;
    }

    public Collection<LightDocumentedElement> getCommandHandlers() {
        return commandHandlers;
    }

    public Collection<LightDocumentedElement> getEventListeners() {
        return eventListeners;
    }

    public Collection<LightDocumentedElement> getRepositories() {
        return repositories;
    }

    public Collection<LightDocumentedElement> getQueries() {
        return queries;
    }

    public Collection<LightDocumentedElement> getQueryResults() {
        return queryResults;
    }

    public Collection<LightDocumentedElement> getCommands() {
        return commands;
    }

    public Collection<LightDocumentedElement> getEvents() {
        return events;
    }

    public Collection<LightDocumentedElement> getConcepts() {
        return concepts;
    }

    public Collection<LightDocumentedElement> getRelations() {
        return relations;
    }

    public String getPrefix() {
        return documentedElement.getPrefix();
    }

    public String getLabel() {
        return documentedElement.getLabel();
    }

}
