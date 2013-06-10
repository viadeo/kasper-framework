// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQueryDTO;

/**
 * A DQO field, declarative DQO class property used to form filters
 * 
 * @param <P> the comparable type of the field
 * @param <DQO> the filtered DQO class
 * @param <F> The filter
 *
 */
public interface IQueryField<P, DQO extends IQueryDQO<?>, F extends IQueryFilter<DQO>> {

	/**
	 * Generic parameter position of payload
	 */
	int PARAMETER_PAYLOAD_POSITION = 0;

	/**
	 * Generic parameter position of DQO
	 */
	int PARAMETER_DQO_POSITION = 1;

	/**
	 * Generic parameter position of filter
	 */
	int PARAMETER_FILTER_POSITION = 2;

	/**
	 * @param dto return the (optional) associated DTO field value
	 * @return the optional value
	 */
	Optional<P> getFieldValue(IQueryDTO dto);

	/**
	 * @return the field name
	 */
	String getName();

	/**
	 * @param name the field name
	 */
	void setName(String name);

	/**
	 * @return a filter for this field
	 */
	F filter();

}
