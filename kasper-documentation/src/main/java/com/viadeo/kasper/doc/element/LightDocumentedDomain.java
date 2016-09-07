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
package com.viadeo.kasper.doc.element;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class LightDocumentedDomain extends LightDocumentedElement<DocumentedDomain> {

    private final Collection<LightDocumentedElement> commands;
    private final Collection<LightDocumentedElement> commandHandlers;
    private final Collection<LightDocumentedElement> queries;
    private final Collection<LightDocumentedElement> queryResults;
    private final Collection<LightDocumentedElement> queryHandlers;
    private final Collection<LightDocumentedElement> events;
    private final Collection<LightDocumentedElement> declaredEvents;
    private final Collection<LightDocumentedElement> referencedEvents;
    private final Collection<LightDocumentedElement> eventListeners;
    private final Collection<LightDocumentedElement> concepts;
    private final Collection<LightDocumentedElement> relations;
    private final Collection<LightDocumentedElement> repositories;
    private final Collection<LightDocumentedElement> sagas;

    private static final Function<AbstractElement, LightDocumentedElement> LIGHTER =
            new Function<AbstractElement, LightDocumentedElement>() {
                @Override
                public LightDocumentedElement apply(final AbstractElement input) {
                    return checkNotNull(input).getLightDocumentedElement();
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
        this.declaredEvents = Collections2.transform(documentedDomain.getDeclaredEvents(), LIGHTER);
        this.referencedEvents = Collections2.transform(documentedDomain.getReferencedEvents(), LIGHTER);
        this.eventListeners = Collections2.transform(documentedDomain.getEventListeners(), LIGHTER);
        this.concepts = Collections2.transform(documentedDomain.getConcepts(), LIGHTER);
        this.relations = Collections2.transform(documentedDomain.getRelations(), LIGHTER);
        this.repositories = Collections2.transform(documentedDomain.getRepositories(), LIGHTER);
        this.sagas = Collections2.transform(documentedDomain.getSagas(), LIGHTER);
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

    public Collection<LightDocumentedElement> getDeclaredEvents() {
        return declaredEvents;
    }

    public Collection<LightDocumentedElement> getReferencedEvents() {
        return referencedEvents;
    }

    public Collection<LightDocumentedElement> getConcepts() {
        return concepts;
    }

    public Collection<LightDocumentedElement> getRelations() {
        return relations;
    }

    public Collection<LightDocumentedElement> getSagas() {
        return sagas;
    }

    public String getPrefix() {
        return documentedElement.getPrefix();
    }

    public String getLabel() {
        return documentedElement.getLabel();
    }

    public String getOwner() {
        return documentedElement.getOwner();
    }

}
