// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;

/**
 * An specialization of <code>CommandHandler</code> allowing to mutate an aggregate.
 *
 * @param <COMMAND> the command class handled by this <code>CommandHandler</code>.
 * @param <AGR> the aggregate class mutated by this <code>CommandHandler</code>.
 */
public interface EntityCommandHandler<COMMAND extends Command, AGR extends AggregateRoot>
        extends CommandHandler<COMMAND>
{

    /**
     * Generic parameter position for the handled entity
     */
    int ENTITY_PARAMETER_POSITION = 1;

    /**
     * @return the aggregate class mutated by this <code>CommandHandler</code>.
     */
    Class<AGR> getAggregateClass();

}
