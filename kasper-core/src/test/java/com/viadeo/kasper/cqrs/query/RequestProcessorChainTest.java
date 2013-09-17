package com.viadeo.kasper.cqrs.query;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import org.junit.Assert;
import org.junit.Test;


public class RequestProcessorChainTest {
    @Test public void testIteratorToChain() {
        DummyRequestProcessor d1 = new DummyRequestProcessor();
        DummyRequestProcessor d2 = new DummyRequestProcessor();
        Iterable<DummyRequestProcessor> chain = Lists.newArrayList(null, d1, null, d2, null);

        RequestProcessorChain actualChain = RequestProcessorChain.makeChain(chain);
        for (DummyRequestProcessor e : Lists.newArrayList(d1, d2)) {
            Assert.assertEquals(e, actualChain.processor);
            actualChain = actualChain.next;
        }
    }

    static class DummyRequestProcessor implements RequestProcessor<Query, QueryPayload> {

        @Override
        public QueryPayload process(Query query, Context context, RequestProcessorChain chain) {
            return null;
        }
    }
}
