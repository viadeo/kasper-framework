// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;

/** The Kasper query gateway, used to answer queries from the platform */
public interface IQueryGateway {

	/**
	 * @param context the query execution context
	 * @param query the query to be answered
	 * @return the Data Transfer Object as an answer
	 */
	<Q extends IQuery, DTO extends IQueryDTO> DTO retrieve(IContext context, Q query) throws KasperQueryException;

}
