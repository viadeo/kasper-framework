// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.AggregateRoot;

/**
 *
 * An aggregate root for Kasper Concept
 *
 * 
 * @see Concept
 * @see com.viadeo.kasper.ddd.Entity
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public interface RootConcept extends Concept, AggregateRoot<KasperID> {

}
