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
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.repository.Repository;

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

    protected final Class<AGR> aggregateClass;
    protected Repository<? extends KasperID, AGR> repository;

    // ------------------------------------------------------------------------

    public AutowiredEntityCommandHandler() {
        super();

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

        this.aggregateClass = (Class<AGR>) entityAssignClass.get();
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<AGR> getAggregateClass() {
        return aggregateClass;
    }

    // ------------------------------------------------------------------------

    /**
     * @param repository the repository related to the aggregate handled by this instance
     * @see AutowiredEntityCommandHandler#setRepository(com.viadeo.kasper.core.component.command.repository.Repository)
     */
    public <ID extends KasperID> void setRepository(final Repository<ID,AGR> repository) {
        this.repository = checkNotNull(repository);
    }

    /**
     * Get the related repository of the entity handled by this command handler
     *
     * @return the repository
     */
    @SuppressWarnings("unchecked")
    public <REPO extends Repository> REPO getRepository() {
        if (null == this.repository) {

            if (null == repositoryManager) {
                throw new KasperCommandException("Unable to resolve repository, no repository manager was provided");
            }

            final Optional<Repository<KasperID,AGR>> optRepo = repositoryManager.getEntityRepository(getAggregateClass());

            if ( ! optRepo.isPresent()) {
                throw new KasperCommandException(String.format(
                        "The entity %s has not been recorded on any domain", getAggregateClass().getSimpleName())
                );
            }

            this.repository = optRepo.get();
        }

        return (REPO) repository;
    }

    // ------------------------------------------------------------------------

}
