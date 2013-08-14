// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification;

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
