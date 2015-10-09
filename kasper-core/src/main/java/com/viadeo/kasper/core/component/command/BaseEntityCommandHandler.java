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

    protected final Class<AGGREGATE> aggregateClass;

    public BaseEntityCommandHandler() {
        super();

        @SuppressWarnings("unchecked")
        final Optional<Class<AGGREGATE>> entityAssignClass = (Optional<Class<AGGREGATE>>) ReflectionGenericsResolver
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

        this.aggregateClass = entityAssignClass.get();
    }

    @Override
    public Class<AGGREGATE> getAggregateClass() {
        return aggregateClass;
    }
}
