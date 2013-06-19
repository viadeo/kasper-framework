// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter;

import java.io.Serializable;

/**
 * Data Query Object
 * 
 * Used to form a query filter to be submitted to a service
 * 
 * Class properties must be public to be used, and extends IQueryField
 * 
 * ex:
 * 
 * private static final class DQOTest extends AbstractQueryDQO<DQOTest, DTOTest> {
 * 		private static final long serialVersionUID = 5709183469642265829L;
 * 		public KasperQueryField<String, DQOTest> name;
 * }
 * 
 * @see IQueryField
 * @see com.viadeo.kasper.cqrs.query.IQueryDTO
 * @see IQueryFilter
 */
public interface IQueryDQO<DQO extends IQueryDQO<DQO>> extends Serializable {

	void init();

}
