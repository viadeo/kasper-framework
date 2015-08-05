// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.event.EntityUpdatedEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is an event concerning directly an entity change, let's mark it with EntityUpdatedEvent interface
 *
 * It's an HelloEvent
 *
 */
@XKasperEvent(
        description = "The recipient buddy of an existing hello message changed",
        action = "changed"
)
public class BuddyChangedForHelloMessageEvent
        extends EntityUpdatedEvent<HelloDomain>
        implements HelloEvent {

    private final KasperID entityId;
    private final String originalForBuddy;
    private final String newForBuddy;

    // ------------------------------------------------------------------------

    public BuddyChangedForHelloMessageEvent(
            final KasperID entityId,
            final String originalForBuddy,
            final String newForBuddy)
    {
        this.entityId = checkNotNull(entityId);
        this.originalForBuddy = checkNotNull(originalForBuddy);
        this.newForBuddy = checkNotNull(newForBuddy);
    }

    // ------------------------------------------------------------------------

    public String getOriginalForBuddy() {
        return this.originalForBuddy;
    }

    public String getNewForBuddy() {
        return this.newForBuddy;
    }

    public KasperID getEntityId() {
        return entityId;
    }
}
