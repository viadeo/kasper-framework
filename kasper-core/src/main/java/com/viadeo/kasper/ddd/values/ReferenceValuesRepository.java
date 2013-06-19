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
 * A reference value repository
 *
 * @param <V> Value
 * @param <P> Payload of value
 * 
 * @see ReferenceValue
 * @see Value
 */
public interface ReferenceValuesRepository<V extends ReferenceValue<P>, P extends Serializable> {

	/**
	 * @param payload the payload from which the repository will try to create a value
	 * @return a new repository value from a valid payload
	 */
	V build(P payload);
	
	/**
	 * @return the default value as defined by the repository
	 */
	V getDefault();
	
}
