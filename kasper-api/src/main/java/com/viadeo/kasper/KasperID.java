// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper;

import java.io.Serializable;

/**
 *
 * Identify uniquely a Kasper Entity (Concept or Relation)
 * IKasperID is a value object
 *
 */
public interface KasperID extends Serializable {

	/**
	 * @return the enclosing id
	 */
	Object getId();

	/**
	 * @return a IKasperID must always be serializable to a String
	 */
	@Override
	String toString();

	/**
	 * Compare to an enclosed value OR another IKasperID
	 * The implementations must support the two cases
	 * 
	 * ie:
	 * 
	 * IdImplA a = new IdImpl(24);
	 * IdImplB b = new IdImpl(24);
	 * 
	 * a.equals(a) is true
	 * b.equals(a) is true
	 * b.equals(24) is true
	 * 
	 */
	@Override
	boolean equals(Object value);
	
	/**
	 * @return the id hashCode
	 * 
	 * IdImplA a = new IdImpl(24);
	 * IdImplB b = new IdImpl(24);
	 * IdImplA c = new IdImpl(42);
	 * 
	 * a.hashCode() == b.hashCode() is true
	 * a.hashCode() == c.hashCode() is false
	 * 
	 */
	@Override
	int hashCode();
	
}
