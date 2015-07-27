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
import com.viadeo.kasper.core.component.command.repository.ClientRepository;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

public abstract class BaseEntityCommandHandler<COMMAND extends Command, AGGREGATE extends AggregateRoot>
        extends BaseCommandHandler<COMMAND>
        implements EntityCommandHandler<COMMAND, AGGREGATE>
{

    // Consistent data container for entity class and repository
    protected static final class ConsistentRepositoryEntity<E extends AggregateRoot> {
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

        public ClientRepository<E> getRepository() {
            return repository;
        }

        public Class<E> getEntityClass() {
            return entityClass;
        }
    }

    protected final transient ConsistentRepositoryEntity<AGGREGATE> consistentRepositoryEntity;

    public BaseEntityCommandHandler() {
        super();

        consistentRepositoryEntity = new ConsistentRepositoryEntity<>();

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

    @Override
    public Class<AGGREGATE> getAggregateClass() {
        return consistentRepositoryEntity.getEntityClass();
    }
}
