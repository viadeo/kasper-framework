// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl.base;

import com.viadeo.kasper.cqrs.query.filter.QueryDQO;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryField;

/**
 * 
 *         A base implementation for Kasper query field
 * 
 * @param <P>
 *            the field payload (Comparable)
 * @param <DQO>
 *            the associated DQO
 * 
 * @see com.viadeo.kasper.cqrs.query.filter.QueryField
 * @see com.viadeo.kasper.cqrs.query.filter.QueryDQO
 */
public class BaseQueryField<P extends Comparable<P>, DQO extends QueryDQO<DQO>>
		extends AbstractQueryField<P, DQO, BaseQueryFilterElement<DQO,P>> {

}
