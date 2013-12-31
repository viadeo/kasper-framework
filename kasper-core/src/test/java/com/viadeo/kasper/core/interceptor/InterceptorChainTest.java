// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.junit.Assert;
import org.junit.Test;

public class InterceptorChainTest {

    @Test
    public void testIteratorToChain() {
        // Given
        final DummyInterceptor d1 = new DummyInterceptor();
        final DummyInterceptor d2 = new DummyInterceptor();
        final Iterable<DummyInterceptor> chain = Lists.newArrayList(null, d1, null, d2, null);

        // When
        final InterceptorChain<Query, QueryResult> actualChain =
                InterceptorChain.makeChain(chain);

        // Then
        InterceptorChain<Query, QueryResult> elt = actualChain;
        for (final DummyInterceptor e : Lists.newArrayList(d1, d2)) {
            Assert.assertEquals(e, elt.actor.get());
            elt = actualChain.next.get();
        }
    }

    // ------------------------------------------------------------------------

    static class DummyInterceptor implements Interceptor<Query, QueryResult> {
        @Override
        public QueryResult process(Query query, Context context, InterceptorChain chain) {
            return null;
        }
    }

}
