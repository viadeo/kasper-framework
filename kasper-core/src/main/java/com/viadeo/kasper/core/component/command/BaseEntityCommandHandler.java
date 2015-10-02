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
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;

public abstract class BaseEntityCommandHandler<COMMAND extends Command, AGGREGATE extends AggregateRoot>
        extends BaseCommandHandler<COMMAND>
        implements EntityCommandHandler<COMMAND, AGGREGATE>
{

//    // Consistent data container for entity class and repository
//    protected static final class ConsistentRepositoryEntity<E extends AggregateRoot> {
//        private Repository<? extends KasperID, E> repository;
//        private Class<E> entityClass;
//
//        @SuppressWarnings("unchecked")
//        void setEntityClass(final Class entityClass) {
//            this.entityClass = (Class<E>) entityClass;
//        }
//
//        void setRepository(final Repository<? extends KasperID, E> repository) {
//            this.repository = repository;
//        }
//
//        public Repository<? extends KasperID, E> getRepository() {
//            return repository;
//        }
//
//        public Class<E> getEntityClass() {
//            return entityClass;
//        }
//    }
//
//    protected final transient ConsistentRepositoryEntity<AGGREGATE> consistentRepositoryEntity;
    protected final Class<AGGREGATE> aggregateClass;

    public BaseEntityCommandHandler() {
        super();

//        consistentRepositoryEntity = new ConsistentRepositoryEntity<>();

        @SuppressWarnings("unchecked")
        final Optional<Class<? extends AggregateRoot>> entityAssignClass = (Optional<Class<? extends AggregateRoot>>) ReflectionGenericsResolver
                .getParameterTypeFromClass(
                        this.getClass(),
                        BaseCommandHandler.class,
                        ENTITY_PARAMETER_POSITION
                );

        if ( ! entityAssignClass.isPresent()) {
            throw new KasperCommandException(
                    "Cannot determine entity type for "
                            + this.getClass().getName()
            );
        }

        this.aggregateClass = (Class<AGGREGATE>) entityAssignClass.get();
    }

    @Override
    public Class<AGGREGATE> getAggregateClass() {
        return aggregateClass;
    }
}
