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
 *         Data transfer object enclosing a collection response. The response represent
 *         a window over a complete list of possible ordered responses
 * 
 * @param <RES> the enclosed unit response type
 * 
 * @see QueryResponse
 */
public interface CollectionQueryResult<RES> extends Iterable<RES>, QueryResult {

	/** Generic parameter position for Data Transfer Object */
	int PARAMETER_RESULT_POSITION = 0;

	/**
	 * Constant that can be used in getTotal() to indicate an infinite number of
	 * possible response elements
	 */
	int INFINITE_TOTAL = -1;

	/** @return the number of elements of this (returned) collection */
	int getCount();

    /** @return the list of Response elements */
    Collection<RES> getList();

}
