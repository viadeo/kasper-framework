// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl.simple;

import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
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
 * @see IQueryField
 * @see IQueryDQO
 */
public class SimpleQueryField<P extends Comparable<P>, DQO extends IQueryDQO<DQO>>
		extends AbstractQueryField<P, DQO, SimpleQueryFilterElement<DQO,P>> {

}
