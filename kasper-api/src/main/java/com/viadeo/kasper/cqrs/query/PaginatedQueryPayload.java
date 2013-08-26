// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

/** A paginated Kasper query */
public interface PaginatedQueryPayload extends QueryPayload {

	/** The requested number of elements. */
	int getCount();

	/** The requested index of the first element in the page. Starts at {@code 0}. */
	int getStartIndex();

}
