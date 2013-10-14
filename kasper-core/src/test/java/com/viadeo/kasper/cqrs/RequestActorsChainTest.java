// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import org.junit.Assert;
import org.junit.Test;

public class RequestActorsChainTest {

    @Test
    public void testIteratorToChain() {
        // Given
        final DummyRequestActor d1 = new DummyRequestActor();
        final DummyRequestActor d2 = new DummyRequestActor();
        final Iterable<DummyRequestActor> chain = Lists.newArrayList(null, d1, null, d2, null);

        // When
        final RequestActorsChain<Query, QueryAnswer> actualChain =
                RequestActorsChain.makeChain(chain);

        // Then
        RequestActorsChain<Query, QueryAnswer> elt = actualChain;
        for (final DummyRequestActor e : Lists.newArrayList(d1, d2)) {
            Assert.assertEquals(e, elt.actor.get());
            elt = actualChain.next.get();
        }
    }

    // ------------------------------------------------------------------------

    static class DummyRequestActor implements RequestActor<Query, QueryAnswer> {
        @Override
        public QueryAnswer process(Query query, Context context, RequestActorsChain chain) {
            return null;
        }
    }

}
