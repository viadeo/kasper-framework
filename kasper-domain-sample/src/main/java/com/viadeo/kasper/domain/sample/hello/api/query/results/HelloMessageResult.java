// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.query.results;

import com.viadeo.kasper.api.component.query.IndexedEntity;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.id.KasperID;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We are indexing an entity, we have to store id and version, let's use an IndexedEntity
 *
 * AbstractIndexedEntity is a base implementation
 *
 * This entity will also be used directly as a result, implements EntityQueryResult
 *
 */
public class HelloMessageResult extends IndexedEntity implements QueryResult {

    public static final String ENTITY_NAME = "Hello";

    private final String message;

    // ------------------------------------------------------------------------

    public HelloMessageResult(final KasperID id,
                              final Long version, final DateTime lastModificationDate,
                              final String message) {
        super(id, ENTITY_NAME, version, lastModificationDate);
        this.message = checkNotNull(message);
    }

    // ------------------------------------------------------------------------

    public String getMessage() {
        return this.message;
    }

}
