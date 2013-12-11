// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.ddd.repository.Repository;

/**
 * Repository holder used by command handler in order to easily access entities repository
 */
public interface RepositoryManager {

    /**
     * Register a new domain repository
     *
     * @param repository the repository to register
     */
    void register(Repository repository);

    /**
     * Get the repository for an entity class
     *
     * @param entityClass the entity class
     * @return the repository responsible for storing this class of entities
     */
    <E extends AggregateRoot> Optional<ClientRepository<E>> getEntityRepository(Class<E> entityClass);

}
