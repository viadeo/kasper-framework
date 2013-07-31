// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.domain.er;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.domain.RootEntityEvent;

/**
 *
 * Event on Kasper Concept
 *
 */
public interface ConceptRootEvent<D extends Domain, C> extends RootEntityEvent<D, C> {

}
