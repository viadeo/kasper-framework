// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.EntityCommandHandler;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 * Base implementation for Kasper entity command handlers
 * 
 * @param <C> Command
 * @param <AGR> the entity (aggregate root)
 * @see com.viadeo.kasper.ddd.AggregateRoot
 * @see com.viadeo.kasper.cqrs.command.EntityCommandHandler
 * @see com.viadeo.kasper.cqrs.command.CommandHandler
 * @see com.viadeo.kasper.ddd.Entity
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public abstract class AbstractEntityCommandHandler<C extends Command, AGR extends AggregateRoot> extends
        AbstractCommandHandler<C> implements EntityCommandHandler<C, AGR> {

    // Consistent data container for entity class and repository
    private static final class Consistent<E extends AggregateRoot> {
        private IRepository<E> repository;
        private Class<E> entityClass;

        @SuppressWarnings("unchecked")
        void setEntityClass(final Class<?> entityClass) {
            this.entityClass = (Class<E>) entityClass;
        }

        @SuppressWarnings("unchecked")
        void setRepository(final IRepository<?> repository) {
            this.repository = (IRepository<E>) repository;
        }
    }

    @SuppressWarnings("rawtypes")
    private final transient Consistent<?> consistent = new Consistent();

    // ------------------------------------------------------------------------

    public AbstractEntityCommandHandler() {
        super();

        //- Extract entity class for further repository lookup ----------------
        // TODO: to check if performance optimization is needed (ConcurrentMap cache)

        @SuppressWarnings("unchecked")
        // Safe
        final Optional<Class<? extends AggregateRoot>> entityAssignClass = (Optional<Class<? extends AggregateRoot>>) ReflectionGenericsResolver
                .getParameterTypeFromClass(this.getClass(), EntityCommandHandler.class,
                        EntityCommandHandler.ENTITY_PARAMETER_POSITION);

        if (!entityAssignClass.isPresent()) {
            throw new KasperCommandException("Cannot determine entity type for " + this.getClass().getName());
        }

        this.consistent.setEntityClass(entityAssignClass.get());
    }

   // ========================================================================

    /**
     * @see com.viadeo.kasper.cqrs.command.EntityCommandHandler#setRepository(com.viadeo.kasper.ddd.IRepository)
     */
    @Override
    public void setRepository(final IRepository<AGR> repository) {
        this.consistent.setRepository(Preconditions.checkNotNull(repository));
    }

    /**
     * @see com.viadeo.kasper.cqrs.command.EntityCommandHandler#getRepository()
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R extends IRepository<AGR>> R getRepository() {
        if (null == this.consistent.repository) {
            if (null == this.getDomainLocator()) {
                throw new KasperCommandException("Unable to resolve repository, no domain locator was provided");
            }
            this.consistent.setRepository(this.getDomainLocator().getEntityRepository(this.consistent.entityClass));
        }
        return (R) this.consistent.repository;
    }

}
