// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.KasperID;
import org.joda.time.DateTime;

/**
 * Can be used as a base interface for all indexed entities
 */
public interface IndexedEntity {

    /**
     * @return the id of the indexed entity
     */
    KasperID getId();

    /**
     * @return the type of the indexed entity
     */
    String getType();

    /**
     * @return the version of the indexed entity
     */
    Long getVersion();
    <I extends IndexedEntity> I setVersion(Long version);

    /**
     * @return the last modification date of the indexed entity
     */
    DateTime getLastModificationDate();
    <I extends IndexedEntity> I setLastModificationDate(final DateTime date);

}
