// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.event.domain;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.Event;

/**
 *
 * Kasper domain event
 * 
 * Mainly all events are domain events, but external systems could send
 * events to the bus, they will not be domain events..
 *
 * @see com.viadeo.kasper.event.Event
 */
public interface DomainEvent<D extends Domain> extends Event {

    int DOMAIN_PARAMETER_POSITION = 0;

}
