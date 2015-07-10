// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import com.viadeo.kasper.api.id.KasperID;

/**
 *
 * The base domain CQRS repository
 *
 * DDD Repository pattern hide the complexity of persisting business objects.
 *
 * CQRS repository does not allow retrieving entities by contract, but very discrete accesses of data
 * can be envisaged on some repositories. These read accesses has to be created for particular business
 * validations required by the model and should be optimized for access, using for instance a
 * dedicated storage index.
 *
 * @param <AGR> Aggregate root
 *
 * @see com.viadeo.kasper.api.domain.Domain
 * @see AggregateRoot
 */
public interface IRepository<AGR extends AggregateRoot> extends org.axonframework.repository.Repository<AGR> {

	/**
	 * Generic parameter position of the AGR
	 */
	int ENTITY_PARAMETER_POSITION = 0;
	
	/**
	 * Initialize repository
	 */
	void init();

    /**
     * Checks if an aggregate if exists
     *
     * @param id the identifier
     * @return true if an aggregate exists with this id
     */
    boolean has(KasperID id);

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * Deprecated design : aggregates should only be loaded, with idea of change,
     * other data must be obtained from a query and passed to the command
     *
     * @param aggregateIdentifier the aggregate identifier to fetch
     * @param expectedVersion the aggregate expected version to fetch
     * @return the fetched aggregate if any
     */
    AGR get(KasperID aggregateIdentifier, Long expectedVersion);

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * Deprecated design : aggregates should only be loaded, with idea of change,
     * other data must be obtained from a query and passed to the command
     *
     * @param aggregateIdentifier the aggregate identifier
     * @return the fetched aggregate if any
     */
    AGR get(KasperID aggregateIdentifier);

}
