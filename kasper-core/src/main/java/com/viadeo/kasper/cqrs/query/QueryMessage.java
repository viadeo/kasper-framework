// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;

import java.io.Serializable;

/**
 * The message used to pass a query to the kasper platform
 *
 * @param <Q> the encapsulated query class
 */
public interface QueryMessage<Q extends Query> extends Serializable {

	/**
	 * @return the encapsulated query
	 */
	Q getQuery();

	/**
	 * @return the query execution context
	 */
	Context getContext();

}
