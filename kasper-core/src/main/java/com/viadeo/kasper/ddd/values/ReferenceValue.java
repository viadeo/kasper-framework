// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.values;

import java.io.Serializable;

/**
 *
 * A reference value stands to be part of a static or slowly-evolving business list
 * ex: understood types, list of accepted values, etc..
 *
 * @param <PAYLOAD> Value payload
 */
public interface ReferenceValue<PAYLOAD extends Serializable> extends Value {
	
	/**
	 * @return the value reference id as it is known in its closed list
	 */
	Long getId();
	
}
