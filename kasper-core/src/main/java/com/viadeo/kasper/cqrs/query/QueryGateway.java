// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;

/** The Kasper query gateway, used to answer queries from the kasper platform */
public interface QueryGateway {

	/**
	 * @param context the query execution context
	 * @param query the query to be answered
	 * @return the Data Transfer Object as an answer
	 */
	<Q extends Query, DTO extends QueryDTO> DTO retrieve(Q query, Context context) throws Exception;

}
