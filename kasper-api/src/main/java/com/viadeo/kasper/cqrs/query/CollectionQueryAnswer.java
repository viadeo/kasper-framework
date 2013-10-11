// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query;

import java.util.Collection;

/**
 * 
 *         Data transfer object enclosing a collection result. The result represent
 *         a window over a complete list of possible ordered results
 * 
 * @param <RES> the enclosed unit result type
 * 
 * @see QueryResult
 */
public interface CollectionQueryAnswer<RES> extends Iterable<RES>, QueryAnswer {

	/** Generic parameter position for Data Transfer Object */
	int PARAMETER_RESULT_POSITION = 0;

	/**
	 * Constant that can be used in getTotal() to indicate an infinite number of
	 * possible result elements
	 */
	int INFINITE_TOTAL = -1;

	/** @return the number of elements of this (returned) collection */
	int getCount();

    /** @return the list of Result elements */
    Collection<RES> getList();

}
