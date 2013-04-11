// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;

/**
 * A Kasper query service
 * 
 * @param <Q> the associated Query
 * @param <DTO> the associated Data Transfer Object
 * 
 * @see IQueryDTO
 * @see IQuery
 */
public interface IQueryService<Q extends IQuery, DTO extends IQueryDTO> {

	/**
	 * Generic parameter position for Data Query Object
	 */
	public static final int PARAMETER_QUERY_POSITION = 0;

	/**
	 * Generic parameter position for Data Transfer Object
	 */
	public static final int PARAMETER_DTO_POSITION = 1;

	/**
	 * Operates the service, retrieve a service DTO satisfying the submitted
	 * filter
	 * 
	 * @param message a query message
	 * @return a filled DTO
	 */
	DTO retrieve(IQueryMessage<Q> message) throws KasperQueryException;

}
