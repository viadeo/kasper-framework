// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import com.viadeo.kasper.IDomain;

import java.util.Set;

/**
 *
 * A domain-internal representation of the domain
 *
 */
public interface IInternalDomain extends IDomain, IUbiquitousLanguageElement {

	/**
	 * @return a set with all domain's entities
	 */
	Set<? extends IEntity> getDomainEntities();

	/**
	 * @param entity
	 * @return the entity repository
	 */
	<E extends IAggregateRoot> IRepository<E> getEntityRepository(E entity);

	/**
	 * @param entityClass the entity class for which to search the repository
	 * @return the entity repository
	 */
	<E extends IAggregateRoot> IRepository<E> getEntityRepository(Class<E> entityClass);

}
