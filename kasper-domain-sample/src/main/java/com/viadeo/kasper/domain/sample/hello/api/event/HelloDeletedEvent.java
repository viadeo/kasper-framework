// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.event.EntityDeletedEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * It's an event about entity deletion, let's mark it with EntityDeletedEvent interface
 *
 * It's an HelloEvent
 *
 */
@XKasperEvent(
        description = "An existing hello message has been deleted",
        action = "deleted"
)
public class HelloDeletedEvent
        extends EntityDeletedEvent<HelloDomain>
        implements HelloEvent {

    private final KasperID entityId;
    private final String forBuddy;

    public HelloDeletedEvent(final KasperID entityId, final String forBuddy) {
        this.entityId = checkNotNull(entityId);
        this.forBuddy = checkNotNull(forBuddy);
    }

    public String getForBuddy() {
        return this.forBuddy;
    }

    public KasperID getEntityId() {
        return entityId;
    }
}
