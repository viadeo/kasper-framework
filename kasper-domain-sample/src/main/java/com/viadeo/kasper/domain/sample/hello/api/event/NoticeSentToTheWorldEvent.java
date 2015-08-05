// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * It's a simple domain event, implement DomainEvent
 *
 * It's an HelloDomainEvent
 *
 */
@XKasperEvent(
        description = "Noticed the world about an important fact",
        action = "sent"
)
public class NoticeSentToTheWorldEvent implements HelloDomainEvent {

    private final String response;

    public NoticeSentToTheWorldEvent(final String response) {
        this.response = checkNotNull(response);
    }

    public String getResponse() {
        return this.response;
    }

}
