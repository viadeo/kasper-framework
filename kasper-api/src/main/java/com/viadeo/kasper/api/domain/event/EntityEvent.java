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
 * A Kasper event related to an entity
 *
 */
public interface EntityEvent<D extends Domain> extends DomainEvent<D> {

}
