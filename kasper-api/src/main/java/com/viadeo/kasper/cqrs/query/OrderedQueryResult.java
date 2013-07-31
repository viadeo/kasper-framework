// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

/**
 * An ordered KasperQuery
 *
 */
public interface OrderedQueryResult extends QueryResult {

	public static enum ORDER {
		ASC, DESC, NONE
	}

}
