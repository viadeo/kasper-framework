// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.query;

/** A paginated Kasper query */
public interface OrderedQuery extends Query {

    public static enum ORDER {
        ASC, DESC, NONE
    }

    ORDER getOrder();

}
