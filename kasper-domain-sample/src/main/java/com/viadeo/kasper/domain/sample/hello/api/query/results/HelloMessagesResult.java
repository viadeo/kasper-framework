// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.query.results;

import com.viadeo.kasper.api.annotation.XKasperQueryResult;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;

import java.util.List;

/**
 * It's a list result, must implement CollectionQueryResult
 *
 * AbstractCollectionQueryResult provides a base implementation
 *
 */
@XKasperQueryResult(description = "A list of Hello messages")
public class HelloMessagesResult extends CollectionQueryResult<HelloMessageResult> {
    public HelloMessagesResult(List<HelloMessageResult> list) {
        super(list);
    }
}
