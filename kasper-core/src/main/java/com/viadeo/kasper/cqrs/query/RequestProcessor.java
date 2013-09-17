package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;

public interface RequestProcessor<INPUT, OUTPUT> {
    OUTPUT process(INPUT input, Context context, RequestProcessorChain<INPUT, OUTPUT> chain) throws Exception;

    public static class DelegatingRequestProcessor<I, O> implements RequestProcessor<I, O> {
        @Override
        public O process(I i, Context context, RequestProcessorChain<I, O> chain) throws Exception {
            return chain.next(i, context);
        }
    }
}