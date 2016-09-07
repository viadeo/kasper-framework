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
package com.viadeo.kasper.domain.sample.hello.command.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.repository.AutowiredRepository;
import com.viadeo.kasper.domain.sample.hello.command.entity.Hello;
import com.viadeo.kasper.domain.sample.hello.common.db.HelloMessagesByBuddyBusinessIndexStore;
import com.viadeo.kasper.domain.sample.hello.common.db.HelloMessagesIndexStore;
import com.viadeo.kasper.domain.sample.hello.common.db.KeyValueStore;

/**
 * The repository is the standard (AND ONLY) place to maintain data integrity of aggregates
 *
 * It is required because Turing machines are not available and we need to persist data
 * in a durable and consistent way.
 *
 * A repository have to guarantee ACID properties :
 *   - Atomicity (the whole or nothing)
 *   - Consistency (after write, the system is in a consistent state)
 *   - Isolation (the order in which this query will be played has no consequency on other queries)
 *   - Durability (Once persisted the information cannot be deleted)
 *
 * - A repository must implement three methods :
 *   - doLoad(id, version)
 *   - doSave(id, object) _managing with creation or update of the aggregate_
 *   - doDelete(object)
 *
 * - A repository can implement an optional method :
 *   - doHas(id)
 *
 * - A repository CAN maintain one or several BUSINESS INDEXES only used to be able to
 *   expose some business rules validation methods (ex: isMemberExistsWithThisEmail()
 *   will be possible because doSave() will maintain a dedicated index with all email adresses)
 *
 *   ACID properties have to be maintained for the entity store and business index(es) at once
 *   if a write to a business index cannot be done, the write to the entity store is roll-backed
 *
 */
@XKasperRepository(description = "Store Hello entities")
public class HelloRepository extends AutowiredRepository<KasperID,Hello> {

    /**
     * The entity store used to store aggregates
     */
    final KeyValueStore store = HelloMessagesIndexStore.db;

    /**
     * A business index storage used to validate a specific business rule
     *    -> does a user is trying to send the same exact message twice ?
     */
    final KeyValueStore businessStore = HelloMessagesByBuddyBusinessIndexStore.db;

    // ------------------------------------------------------------------------

    /**
     * Utility method : construct a business key from a message and a buddy
     * --> no need to store the whole message, a hash is sufficient
     */
    private String businessKey(final Hello aggregate) {
        return String.format(
            "%d-%d",
            aggregate.getForBuddy().hashCode(),
            aggregate.getMessage().hashCode()
        );
    }

    // ------------------------------------------------------------------------

    /**
     * Does not manage with version here in this sample
     */
    @Override
    public Optional<Hello> doLoad(final KasperID aggregateIdentifier,
                                     final Long expectedVersion) {

        final Optional<Hello> hello = store.get(aggregateIdentifier);

        if (hello.isPresent()) {
            /**
             * As we do directly stored the aggregate in memory
             * (it will be the same for a direct serialization in the store)
             * we do not need to build a new entity.
             *
             * In case you need to build an entity (after storage on a columnar storage for instance)
             * you'll have to call the aggregate special builder (see Hello aggregate build() method)
             *
             * In case you decided (good !) to not really delete the aggregate but mark it as deleted
             * through doSave() forwarding in doDelete() method, think about detecting this flag and
             * return here an AggregateDeletedException if the flag is active
             */
            return Optional.of(hello.get());

        } else{
            return Optional.absent();
        }
    }

    @Override
    protected void doSave(final Hello aggregate) {

        /**
         * No need here to detect save or update difference as we are using a simple key/value store.
         *
         * In case your are using a columnar storage, you have two different strategies to detect
         * the save or the update :
         *
         * 1/ test aggregate.getVersion(), the version will be null in case of aggregate creation
         * 2/ override doUpdate(), so the creation will be done by doSave() and update through doUpdate()
         *
         */

        /* Maintain entity store and business index consistency */
        store.set(aggregate.getIdentifier(), aggregate);
        try {
            businessStore.set(businessKey(aggregate), true);
        } catch (final Exception e) {
            store.del(aggregate.getIdentifier());
            throw e;
        }
    }

    /**
     * It's a good practice to never delete data, try to just call doSave()
     * adding necessary data to detect it as deleted on next doLoad() try
     * (return AggregateDeletedException)
     */
    @Override
    protected void doDelete(final Hello aggregate) {
        this.doSave(aggregate);
    }

    /**
     * Useful if you need this use case from an handler, no need to load() or get()
     * the aggregate (as a reminder load() will automatically schedule a save())
     */
    @Override
    protected boolean doHas(final KasperID id) {
        return store.has(id);
    }

    // ------------------------------------------------------------------------

    /**
     * Uses the business index to search for existing identical message
     *
     * Access to this method in your handlers using :
     *
     *    this.getRepository().business().hasTheSameMessage()
     *
     * @param aggregate an aggregate
     * @return true if it's the same message, false otherwise
     */
    public boolean hasTheSameMessage(final Hello aggregate) {
        return businessStore.has(businessKey(aggregate));
    }


}
