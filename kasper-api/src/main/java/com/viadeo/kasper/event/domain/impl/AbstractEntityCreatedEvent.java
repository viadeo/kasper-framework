// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.event.domain.EntityEvent;
import org.joda.time.DateTime;

/**
 *
 * Base implementation for created entity events
 *
 * @see com.viadeo.kasper.event.domain.EntityEvent
 */
public abstract class AbstractEntityCreatedEvent<D extends Domain>
        extends AbstractEntityEvent<D>
        implements EntityCreatedEvent<D> {

    protected AbstractEntityCreatedEvent(final KasperID id,
                                         final DateTime lastModificationDate) {
        super(id, lastModificationDate);
    }

    protected AbstractEntityCreatedEvent(final Context context,
                                  final KasperID id,
                                  final DateTime lastModificationDate) {
        super(context, id, lastModificationDate);
    }

}
