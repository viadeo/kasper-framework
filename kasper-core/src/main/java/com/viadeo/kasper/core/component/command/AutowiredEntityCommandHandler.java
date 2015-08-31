// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.ddd.IRepository;
import com.viadeo.kasper.core.component.command.repository.ClientRepository;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base implementation for an auto wired Kasper entity command handler.
 *
 * @param <C> Command
 * @param <AGR> the entity (aggregate root)
 *
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot
 * @see AutowiredEntityCommandHandler
 * @see AutowiredCommandHandler
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.Entity
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot
 */
public abstract class AutowiredEntityCommandHandler<C extends Command, AGR extends AggregateRoot>
        extends AutowiredCommandHandler<C>
        implements EntityCommandHandler<C,AGR>, WirableCommandHandler<C>
{


    protected final transient BaseEntityCommandHandler.ConsistentRepositoryEntity<AGR> consistentRepositoryEntity;

    // ------------------------------------------------------------------------

    public AutowiredEntityCommandHandler() {
        super();
        consistentRepositoryEntity = new BaseEntityCommandHandler.ConsistentRepositoryEntity<>();

        @SuppressWarnings("unchecked")
        final Optional<Class<? extends AggregateRoot>> entityAssignClass = (Optional<Class<? extends AggregateRoot>>) ReflectionGenericsResolver
                .getParameterTypeFromClass(
                        this.getClass(),
                        AutowiredEntityCommandHandler.class,
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

    @Override
    public Class<AGR> getAggregateClass() {
        return consistentRepositoryEntity.getEntityClass();
    }

    // ------------------------------------------------------------------------

    /**
     * @param repository the repository related to the aggregate handled by this instance
     * @see AutowiredEntityCommandHandler#setRepository(com.viadeo.kasper.core.component.command.aggregate.ddd.IRepository)
     */
    public void setRepository(final IRepository<AGR> repository) {
        this.consistentRepositoryEntity.setRepository(
                new ClientRepository<>(checkNotNull(repository))
        );
    }

    /**
     * Get the related repository of the entity handled by this command handler
     *
     * @return the repository
     */
    @SuppressWarnings("unchecked")
    public ClientRepository<AGR> getRepository() {
        if (null == this.consistentRepositoryEntity.getRepository()) {

            if (null == repositoryManager) {
                throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
            }

            final Optional<ClientRepository<AGR>> optRepo =
                    repositoryManager.getEntityRepository(this.consistentRepositoryEntity.getEntityClass());

            if ( ! optRepo.isPresent()) {
                throw new KasperCommandException(String.format(
                        "The entity %s has not been recorded on any domain",
                        this.consistentRepositoryEntity.getEntityClass().getSimpleName())
                );
            }

            this.consistentRepositoryEntity.setRepository(optRepo.get());
        }

        return this.consistentRepositoryEntity.getRepository();
    }

    /**
     * Get the related repository of the specified entity class
     *
     * @param entityClass the class of the entity
     * @param <E> the type of the entity
     * @return the entity repository
     */
    public <E extends AggregateRoot> Optional<ClientRepository<E>> getRepositoryOf(final Class<E> entityClass) {
        if (null == repositoryManager) {
            throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
        }

        return repositoryManager.getEntityRepository(entityClass);
    }

    // ------------------------------------------------------------------------

}
