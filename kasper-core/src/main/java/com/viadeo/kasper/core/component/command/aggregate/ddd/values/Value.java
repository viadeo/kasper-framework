// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.aggregate.ddd.values;

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
public interface Value extends Serializable {

	/**
	 * @return a string serialization
	 */
	String toString();
	
	/**
	 * @param otherValue an other value
	 * @return true if two objects or results are equal
	 *
	 * can be used to compare an IValue with a result for instance
	 */
	boolean equals(Object otherValue);
	
	/**
	 * @return the value hashcode
	 */
	int hashCode();
		
}
