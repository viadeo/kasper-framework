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
 *         Data transfer object enclosing a collection result. The DTO represent
 *         a window over a complete list of possible ordered results
 * 
 * @param <DTO>
 *            the enclosed DTO type
 * 
 * @see QueryDTO
 */
public interface QueryCollectionDTO<DTO extends QueryDTO> extends
        QueryDTO, Iterable<DTO> {

	/** Generic parameter position for Data Transfer Object */
	int PARAMETER_DTO_POSITION = 0;

	/**
	 * Constant that can be used in getTotal() to indicate an infinite number of
	 * possible result elements
	 */
	int INFINITE_TOTAL = -1;

	/** @return the number of elements of this (returned) collection */
	int getCount();

    /** @return the list of DTO elements */
    Collection<DTO> getList();

}