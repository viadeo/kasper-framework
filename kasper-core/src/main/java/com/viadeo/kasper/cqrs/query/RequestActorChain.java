package com.viadeo.kasper.cqrs.query;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;

import java.util.Iterator;

public class RequestActorChain<INPUT, OUTPUT> {

    private static final RequestActorChain TAIL = new RequestActorChain(null) {
        @Override
        public Object next(Object i, Context context) throws Exception {
            throw new IllegalStateException("Reached chain tail without handling the input request");
        }
    };

    @VisibleForTesting
    final RequestActor<INPUT, OUTPUT> actor;
    @VisibleForTesting
    final RequestActorChain next;

    public static <I, O> RequestActorChain<I, O> makeChain(RequestActor<I, O>...chain) {
        return makeChain(Lists.newArrayList(chain));
    }

    public static <I, O> RequestActorChain<I, O> makeChain(Iterable<? extends RequestActor<I, O>> chain) {
        return makeChain(chain.iterator());
    }

    public static <I, O> RequestActorChain<I, O> makeChain(Iterator<? extends RequestActor<I, O>> chain) {
        RequestActor<I, O> actor = null;

        // so we can support null elements in the iterator
        while (actor == null && chain.hasNext()) {
            actor = chain.next();
        }

        if (actor != null)
            return makeChain(chain).withPrevious(actor);


        return TAIL;
    }

    public RequestActorChain(RequestActor<INPUT, OUTPUT> actor) {
        this.actor = actor;
        this.next = TAIL;
    }

    public RequestActorChain(RequestActor<INPUT, OUTPUT> actor, RequestActorChain next) {
        this.actor = actor;
        this.next = next;
    }

    public OUTPUT next(INPUT input, Context context) throws Exception {
        return actor.process(input, context, next);
    }

    public RequestActorChain withPrevious(RequestActor<INPUT, OUTPUT> previous) {
        return new RequestActorChain(previous, this);
    }
}
