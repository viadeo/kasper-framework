// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.aggregate;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.ddd.Entity;

/**
 *
 * An aggregate root for Kasper Concept
 *
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.Entity
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot
 */
public class Concept
        extends AggregateRoot<KasperID>
        implements Entity {

}
