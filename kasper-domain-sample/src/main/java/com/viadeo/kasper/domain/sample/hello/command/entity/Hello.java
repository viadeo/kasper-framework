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
package com.viadeo.kasper.domain.sample.hello.command.entity;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperEntityStoreCreator;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.event.BuddyChangedForHelloMessageEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloCreatedEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloDeletedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Hello is an aggregate root (the root of an entity to be persisted as a whole)
 * It's a concept (since it's not a relation !)
 *
 * So it's a ROOT CONCEPT
 *
 * AbstractRootConcept provides a base implementation
 *
 * - An aggregate IS NOT A POJO
 * - An aggregate CANNOT CONTAINS SETTER but BUSINESS METHODS
 * - An aggregate SHOULD NOT CONTAINS getters UNLESS ITS NECESSARY (by the repository or specific handlers)
 *
 * - Aggregate MUST be written in EVENT-SOURCING style :
 *     - its methods does not mutate its state (do not change values of the aggregate)
 *     - its methods apply() itself to BUSINESS EVENTS
 *     - mutation (state changing) is made through internal EVENT HANDLERS
 *     - protected methods annotated with @EventHandler and accepting
 *             the concerned event as parameter
 *
 * - An aggregate MUST call setId() in the constructing event handler
 * - An aggregate will generate events, so it generally require to be provided
 *   with the current context in its constructor
 *
 * In fact an aggregate is the MAIN EVENT GENERATION PLACE of the platform
 *
 */
@XKasperConcept( /* Required annotation to define the sticked domain */
        domain = HelloDomain.class,
        label = "Hello message",
        description = "An hello message sent to a buddy"
)
public class Hello extends Concept {
    private static final long serialVersionUID = -3054267595839319426L;

    private static final String UNKNOWN = "unknown";

    private String message = UNKNOWN;
    private String forBuddy = UNKNOWN;

    // ------------------------------------------------------------------------

    /**
     * Aggregate builder
     *
     * Demonstration only : not really needed in the current case
     *
     * If the repository needs to create an instance directly from a columnar entity storage
     * it has to choose between two strategies depending on what it can do :
     *
     * 1/ Generate events from the storage and call the aggregate handlers which have to be public (@deprecated)
     * 2/ Call a dedicated constructor which does not use apply()
     * 3/ Provide an internal static builder (preferred way)
     *
     * In cases 2/ and 3/ annotate your constructor or your builder with the @XKasperEntityStoreCreator for additional semantics
     *
     * @param id a kasper id
     * @param message a message
     * @param forBuddy the recipient
     * @return an instance of Hello
     */
    @XKasperEntityStoreCreator
    public static Hello build(final KasperID id, final String message, final String forBuddy) {
        final Hello hello = new Hello();
        hello.setId(checkNotNull(id));
        hello.message = checkNotNull(message);
        hello.forBuddy = checkNotNull(forBuddy);
        return hello;
    }

    public Hello() {
        /**
         * Empty constructor : used by event-sourced repositories
         *
         * Event if this entity is not currently stored using an event-sourced repository
         * you cannot infer that state : repository is a separate implementation and you need
         * to install a loosely coupled link between the aggregate and its repository
         * from the aggregate poitn of view
         */
    }

    public Hello(final KasperID id, final String message, final String forBuddy) {

        /* You can place here parameters BUSINESS validation rules if required */

        apply(new HelloCreatedEvent(
                checkNotNull(id),
                checkNotNull(message),
                checkNotNull(forBuddy)
        ));
    }

    @EventHandler
    protected void onHelloCreated(final HelloCreatedEvent event) {

        /* Sets the aggregate id - REPOSITORY WILL NOT ACCEPT AGGREGATES WITHOUT ID */
        this.setId(event.getEntityId());

        /* Mutate (construct) the aggregate */
        this.message = event.getMessage();
        this.forBuddy = event.getForBuddy();
    }

    // ------------------------------------------------------------------------

    /* required by repository for composing the business key */
    public String getMessage() {
        return this.message;
    }

    /* required by repository for composing the business key */
    public String getForBuddy() {
        return this.forBuddy;
    }

    // ------------------------------------------------------------------------

    /**
     * Every business method need the mutation context
     * @param forBuddy the recipient
     */
    public void changeBuddy(final String forBuddy) {

        /* You can place here parameters BUSINESS validation rules if required */

        apply(new BuddyChangedForHelloMessageEvent(
                this.getEntityId(),
                this.forBuddy,
                forBuddy
        ));
    }

    @EventHandler
    public void onBuddyChanged(final BuddyChangedForHelloMessageEvent event) {
        /* Mutate (change state of) the aggregate */
        this.forBuddy = event.getNewForBuddy();
    }

    // ------------------------------------------------------------------------

    public void delete() {
        apply(new HelloDeletedEvent(this.getEntityId(), this.forBuddy));
    }

    @EventHandler
    public void onBuddyDeleted(final HelloDeletedEvent event) {
        /**
         * mark as deleted
         *
         * (if this aggregate has previously been loaded, the repository doDelete() method
         * will then be automatically called on unit of work commit)
         *
         */
        this.markDeleted();
    }

}
