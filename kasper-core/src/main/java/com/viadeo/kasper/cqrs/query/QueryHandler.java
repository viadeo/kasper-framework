// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

/**
 * A Kasper query handler
 *
 * @param <Q> the associated Query
 * @param <RESULT> the associated Data Transfer Object
 *
 * @see QueryResponse
 * @see Query
 */
public interface QueryHandler<Q extends Query, RESULT extends QueryResult> {

	/**
	 * Generic parameter position for Data Query Object
	 */
	int PARAMETER_QUERY_POSITION = 0;

	/**
	 * Generic parameter position for Data Transfer Object
	 */
	int PARAMETER_RESULT_POSITION = 1;

	/**
	 * Operates the handler, retrieve an handler Response satisfying the submitted
	 * filter
     *
     * You have to implement at least one retrieve() method
	 *
	 * @param message a message encapsulating the query to result
	 * @return a filled Response
	 */
	QueryResponse<RESULT> retrieve(QueryMessage<Q> message) throws Exception;

}


