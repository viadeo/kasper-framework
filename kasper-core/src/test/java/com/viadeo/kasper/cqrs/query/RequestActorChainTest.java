package com.viadeo.kasper.cqrs.query;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import org.junit.Assert;
import org.junit.Test;


public class RequestActorChainTest {
    @Test public void testIteratorToChain() {
        DummyRequestActor d1 = new DummyRequestActor();
        DummyRequestActor d2 = new DummyRequestActor();
        Iterable<DummyRequestActor> chain = Lists.newArrayList(null, d1, null, d2, null);

        RequestActorChain actualChain = RequestActorChain.makeChain(chain);
        for (DummyRequestActor e : Lists.newArrayList(d1, d2)) {
            Assert.assertEquals(e, actualChain.actor);
            actualChain = actualChain.next;
        }
    }

    static class DummyRequestActor implements RequestActor<Query, QueryPayload> {

        @Override
        public QueryPayload process(Query query, Context context, RequestActorChain chain) {
            return null;
        }
    }
}
