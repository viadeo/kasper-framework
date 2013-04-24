// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.event.domain;

import com.viadeo.kasper.event.IEvent;

/**
 *
 * Kasper domain event
 * 
 * Mainly all events are domain events, but external systems could send
 * events to the bus, they will not be domain events..
 *
 * @see IEvent
 */
public interface IDomainEvent extends IEvent {

}
