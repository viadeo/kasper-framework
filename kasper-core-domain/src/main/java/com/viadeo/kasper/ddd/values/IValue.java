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
 * Kasper Immutable Value Objects
 *
 * Martin Fowler : 
 * 
 * In P of EAA I described Value Object as a small object such as a Money or date range object. Their key property is that 
 * they follow value semantics rather than reference semantics.
 *
 * You can usually tell them because their notion of equality isn't based on identity, instead two value objects are equal 
 * if all their fields are equal. Although all fields are equal, you don't need to compare all fields if a subset is unique - 
 * for example currency codes for currency objects are enough to test equality.
 * 
 * A general heuristic is that value objects should be entirely immutable. If you want to change a value object you should 
 * replace the object with a new one and not be allowed to update the values of the value object itself - updatable value 
 * objects lead to aliasing problems.
 * 
 * Early J2EE literature used the term value object to describe a different notion, what I call a Data Transfer Object. 
 * They have since changed their usage and use the term Transfer Object instead.
 * 
 */
public interface IValue extends Serializable {

	/**
	 * @return
	 */
	String toString();
	
	/**
	 * @param _otherValue
	 * @return true if two objects or payloads are equal
	 *
	 * can be used to compare an IValue with a payload for instance
	 */
	boolean equals(Object _otherValue);
	
	/**
	 * @return the value hashcode
	 */
	int hashCode();
		
}
