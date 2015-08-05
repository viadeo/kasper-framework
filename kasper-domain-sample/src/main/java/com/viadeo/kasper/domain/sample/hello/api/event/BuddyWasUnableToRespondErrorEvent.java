// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.event.ErrorEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.response.CoreReasonCode;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * It's an error event, lets clearly mark this quality implementing ErrorEvent
 *
 * It's an HelloDomainEvent
 *
 */
@XKasperEvent(
        description = "The recipient of an hello message responded to it",
        action = "responded"
)
public class BuddyWasUnableToRespondErrorEvent
        extends ErrorEvent
        implements HelloDomainEvent {

    private final KasperID fromHello;
    private final String response;

    // ------------------------------------------------------------------------

    public BuddyWasUnableToRespondErrorEvent(
            final KasperID fromHello,
            final String response) {
        super(CoreReasonCode.INTERNAL_COMPONENT_ERROR, response);
        this.fromHello = checkNotNull(fromHello);
        this.response = checkNotNull(response);
    }

    // ------------------------------------------------------------------------

    public KasperID getFromHello() {
        return this.fromHello;
    }

    public String getResponse() {
        return this.response;
    }

}
