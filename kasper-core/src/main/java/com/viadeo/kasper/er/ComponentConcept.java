// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.ddd.ComponentEntity;

/**
 *
 * A Kasper Concept which is owned by an upper Concept or an aggregate root
 *
 * @param <R> The parent root concept
 * 
 * @see Concept
 */
public interface ComponentConcept<R extends RootConcept>
	extends ComponentEntity<R>, Concept {
	
}
