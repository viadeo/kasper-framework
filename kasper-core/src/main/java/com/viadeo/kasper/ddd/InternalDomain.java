// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import com.viadeo.kasper.Domain;

import java.util.Set;

/**
 *
 * A domain-internal representation of the domain
 *
 */
public interface InternalDomain extends Domain, UbiquitousLanguageElement {

	/**
	 * @return a set with all domain's entities
	 */
	Set<? extends Entity> getDomainEntities();

	/**
	 * @param entity
	 * @return the entity repository
	 */
	<E extends AggregateRoot> Repository<E> getEntityRepository(E entity);

	/**
	 * @param entityClass the entity class for which to search the repository
	 * @return the entity repository
	 */
	<E extends AggregateRoot> Repository<E> getEntityRepository(Class<E> entityClass);

}
