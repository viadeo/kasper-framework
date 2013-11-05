// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.KasperID;
import org.joda.time.DateTime;

public interface EntityQueryResult extends QueryResult {

    KasperID getId();

    String getType();

    DateTime getLastModificationTime();

}
