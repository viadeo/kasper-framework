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
package com.viadeo.kasper.core.component.command.aggregate.ddd.specification;

/**
 *
 * A specification pattern implementation
 *
 ******
 * In computer programming, the specification pattern is a particular software design pattern, whereby business rules can
 * be recombined by chaining the business rules together using boolean logic.
 * 
 * A specification pattern outlines a business rule that is combinable with other business rules. In this pattern, a unit
 * of business logic inherits its functionality from the abstract aggregate Composite Specification class. The Composite
 * Specification class has one function called IsSatisfiedBy that returns a boolean value. After instantiation, the
 * specification is "chained" with other specifications, making new specifications easily maintainable, yet highly
 * customizable business logic.
 * (source: Wikipedia)
 ******
 *
 * @param <T> The object class on which this specification operates
 *
 */
public interface ISpecification<T> {

	/**
	 * @param entity the entity on which to check specification satisfaction
	 * @return true if the entity satisfies the specification
	 */
	boolean isSatisfiedBy(T entity);

	/**
	 * @param entity entity the entity on which to check specification satisfaction
	 * @param errorMessage the error message instance to be set if an error occurs
	 * @return true if the entity satisfies the specification
	 */
	boolean isSatisfiedBy(T entity, SpecificationErrorMessage errorMessage);
	
	/**
	 * @param specification to compose with AND operator
	 * @return a new composed specification
	 */
	ISpecification<T> and(ISpecification<T> specification);

	/**
	 * @param specification to compose with OR operator
	 * @return a new composed specification
	 */
	ISpecification<T> or(ISpecification<T> specification);

	/**
	 * @return a new inversed specification
	 */
	ISpecification<T> not();

}
