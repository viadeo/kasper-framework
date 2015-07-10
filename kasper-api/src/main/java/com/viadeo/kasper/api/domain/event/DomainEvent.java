// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.api.domain.event;

import com.viadeo.kasper.api.domain.Domain;

/**
 *
 * Kasper domain event marker
 * 
 * Mainly all events are domain events, but external systems could send
 * events to the bus, they will not be domain events..
 *
 * @see Event
 */
public interface DomainEvent<DOMAIN extends Domain> extends Event {

    public static int DOMAIN_PARAMETER_POSITION = 0;

}
