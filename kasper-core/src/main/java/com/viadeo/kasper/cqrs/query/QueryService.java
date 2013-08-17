// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

/**
 * A Kasper query service
 *
 * @param <Q> the associated Query
 * @param <RES> the associated Data Transfer Object
 *
 * @see QueryResult
 * @see Query
 */
public interface QueryService<Q extends Query, RES extends QueryResult> {

	/**
	 * Generic parameter position for Data Query Object
	 */
	int PARAMETER_QUERY_POSITION = 0;

	/**
	 * Generic parameter position for Data Transfer Object
	 */
	int PARAMETER_RESULT_POSITION = 1;

	/**
	 * Operates the service, retrieve a service Result satisfying the submitted
	 * filter
     *
     * You have to implement at least one retrieve() method
	 *
	 * @param message a message encapsulating the query to answer
	 * @return a filled Result
	 */
	RES retrieve(QueryMessage<Q> message) throws Exception;

}


