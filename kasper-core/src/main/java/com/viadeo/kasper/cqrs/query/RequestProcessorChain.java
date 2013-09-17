package com.viadeo.kasper.cqrs.query;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.impl.QueryCacheProcessor;
import com.viadeo.kasper.cqrs.query.impl.QueryServiceProcessor;

import java.util.Iterator;

public class RequestProcessorChain<INPUT, OUTPUT> {

    private static final RequestProcessorChain TAIL = new RequestProcessorChain(null) {
        @Override
        public Object next(Object i, Context context) throws Exception {
            throw new IllegalStateException("Reached chain tail without handling the input request");
        }
    };

    @VisibleForTesting
    final RequestProcessor<INPUT, OUTPUT> processor;
    @VisibleForTesting
    final RequestProcessorChain next;

    public static <I, O> RequestProcessorChain<I, O> makeChain(RequestProcessor<I, O>...chain) {
        return makeChain(Lists.newArrayList(chain));
    }

    public static <I, O> RequestProcessorChain<I, O> makeChain(Iterable<? extends RequestProcessor<I, O>> chain) {
        return makeChain(chain.iterator());
    }

    public static <I, O> RequestProcessorChain<I, O> makeChain(Iterator<? extends RequestProcessor<I, O>> chain) {
        RequestProcessor<I, O> processor = null;

        // so we can support null elements in the iterator
        while (processor == null && chain.hasNext()) {
            processor = chain.next();
        }

        if (processor != null)
            return makeChain(chain).withPrevious(processor);


        return TAIL;
    }

    public RequestProcessorChain(RequestProcessor<INPUT, OUTPUT> processor) {
        this.processor = processor;
        this.next = TAIL;
    }

    public RequestProcessorChain(RequestProcessor<INPUT, OUTPUT> processor, RequestProcessorChain next) {
        this.processor = processor;
        this.next = next;
    }

    public OUTPUT next(INPUT input, Context context) throws Exception {
        return processor.process(input, context, next);
    }

    public RequestProcessorChain withPrevious(RequestProcessor<INPUT, OUTPUT> previous) {
        return new RequestProcessorChain(previous, this);
    }
}
