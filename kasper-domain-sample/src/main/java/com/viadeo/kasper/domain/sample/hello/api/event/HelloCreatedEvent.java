// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * It's an event about entity creation, let's mark it with EntityCreatedEvent interface
 *
 * It's an HelloEvent
 *
 */
@XKasperEvent(
        description = "A new hello message has been created",
        action = "created"
)
public class HelloCreatedEvent
        extends EntityCreatedEvent<HelloDomain>
        implements HelloEvent {

    private final String message;
    private final String forBuddy;

    // ------------------------------------------------------------------------

    public HelloCreatedEvent(
            final KasperID entityId,
            final String message,
            final String forBuddy)
    {
        super(entityId);
        this.message = checkNotNull(message);
        this.forBuddy = checkNotNull(forBuddy);
    }

    // ------------------------------------------------------------------------

    public String getMessage() {
        return this.message;
    }

    public String getForBuddy() {
        return this.forBuddy;
    }

}
