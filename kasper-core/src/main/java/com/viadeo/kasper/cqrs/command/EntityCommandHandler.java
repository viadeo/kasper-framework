// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for Kasper entity command handlers
 *
 * @param <C> Command
 * @param <AGR> the entity (aggregate root)
 * @see com.viadeo.kasper.ddd.AggregateRoot
 * @see EntityCommandHandler
 * @see com.viadeo.kasper.cqrs.command.CommandHandler
 * @see com.viadeo.kasper.ddd.Entity
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public abstract class EntityCommandHandler<C extends Command, AGR extends AggregateRoot>
        extends CommandHandler<C> {

    /**
     * Generic parameter position for the handled command
     */
    public static final int COMMAND_PARAMETER_POSITION = 0;

    /**
     * Generic parameter position for the handled entity
     */
    public static final int ENTITY_PARAMETER_POSITION = 1;

    // Consistent data container for entity class and repository
    private static final class ConsistentRepositoryEntity<E extends AggregateRoot> {
        private ClientRepository<E> repository;
        private Class<E> entityClass;

        @SuppressWarnings("unchecked")
        void setEntityClass(final Class entityClass) {
            this.entityClass = (Class<E>) entityClass;
        }

        @SuppressWarnings("unchecked")
        void setRepository(final ClientRepository repository) {
            this.repository = (ClientRepository<E>) repository;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private final transient ConsistentRepositoryEntity<AGR> consistentRepositoryEntity =
            new ConsistentRepositoryEntity();

    // ------------------------------------------------------------------------

    public EntityCommandHandler() {
        super();

        //- Extract entity class for further repository lookup ----------------
        // TODO: to check if performance optimization is needed (ConcurrentMap cache)

        @SuppressWarnings("unchecked")
        // Safe
        final Optional<Class<? extends AggregateRoot>> entityAssignClass = (Optional<Class<? extends AggregateRoot>>) ReflectionGenericsResolver
                .getParameterTypeFromClass(
                        this.getClass(),
                        EntityCommandHandler.class,
                        ENTITY_PARAMETER_POSITION
                );

        if ( ! entityAssignClass.isPresent()) {
            throw new KasperCommandException(
                    "Cannot determine entity type for "
                            + this.getClass().getName()
            );
        }

        this.consistentRepositoryEntity.setEntityClass(entityAssignClass.get());
    }

    // ------------------------------------------------------------------------

    /**
     * @see EntityCommandHandler#setRepository(com.viadeo.kasper.ddd.IRepository)
     */
    public void setRepository(final IRepository<AGR> repository) {
        this.consistentRepositoryEntity.setRepository(
                new ClientRepository<AGR>(checkNotNull(repository))
        );
    }

    /**
     * Get the related repository of the entity handled by this command handler
     *
     * @return the repository
     */
    @SuppressWarnings("unchecked")
    public ClientRepository<AGR> getRepository() {
        if (null == this.consistentRepositoryEntity.repository) {

            if (null == repositoryManager) {
                throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
            }

            final Optional<ClientRepository<AGR>> optRepo =
                    repositoryManager.getEntityRepository(this.consistentRepositoryEntity.entityClass);

            if ( ! optRepo.isPresent()) {
                throw new KasperCommandException(String.format(
                        "The entity %s has not been recorded on any domain",
                        this.consistentRepositoryEntity.entityClass.getSimpleName())
                );
            }

            this.consistentRepositoryEntity.setRepository(optRepo.get());
        }

        return this.consistentRepositoryEntity.repository;
    }

    /**
     * Get the related repository of the specified entity class
     * @return the entity repository
     */
    public <E extends AggregateRoot> Optional<ClientRepository<E>> getRepositoryOf(final Class<E> entityClass) {
        if (null == repositoryManager) {
            throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
        }

        return repositoryManager.getEntityRepository(entityClass);
    }

}
