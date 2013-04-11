// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import java.io.Serializable;

import com.viadeo.kasper.context.IContext;

/**
 * The message used to vehiculate a query to the platform
 *
 * @param <Q> the encapsulated query class
 */
public interface IQueryMessage<Q extends IQuery> extends Serializable {

	/**
	 * @return the encapsulated query
	 */
	Q getQuery();

	/**
	 * @return the query execution context
	 */
	IContext getContext();

}
