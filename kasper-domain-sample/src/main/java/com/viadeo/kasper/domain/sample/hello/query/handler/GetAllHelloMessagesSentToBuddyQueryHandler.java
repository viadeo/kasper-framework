// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.query.handler;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.exception.KasperQueryException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryFilter;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.query.GetAllHelloMessagesSentToBuddyQuery;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessageResult;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessagesResult;
import com.viadeo.kasper.domain.sample.hello.common.db.HelloMessagesIndexStore;
import com.viadeo.kasper.domain.sample.hello.common.db.KeyValueStore;
import com.viadeo.kasper.domain.sample.hello.query.handler.adapters.NormalizeBuddyQueryInterceptor;

import java.util.Collection;
import java.util.Map;

/** Required annotation to define the sticked domain */
@XKasperQueryHandler(domain = HelloDomain.class)
/** Optional annotation to define which interceptors will be applied on each query before handling */
@XKasperQueryFilter({NormalizeBuddyQueryInterceptor.class})
public class GetAllHelloMessagesSentToBuddyQueryHandler
        extends AutowiredQueryHandler<GetAllHelloMessagesSentToBuddyQuery, HelloMessagesResult> {

    private KeyValueStore store = HelloMessagesIndexStore.db;

    @Override
    @SuppressWarnings("unchecked")
    public QueryResponse<HelloMessagesResult> handle(final GetAllHelloMessagesSentToBuddyQuery query) throws KasperQueryException {

        final String forBuddy = query.getForBuddy();
        Collection<HelloMessageResult> ret = Lists.newArrayList();

        if (store.has(forBuddy)) {
            /** Index directly contains the structure to be returned, no manipulation needed here */
            ret = ((Map<KasperID, HelloMessageResult>) store.get(forBuddy).get()).values();
        }

        return QueryResponse.of(new HelloMessagesResult(Lists.newArrayList(ret)));
    }

}
