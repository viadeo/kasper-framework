// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * An element of an actors chain
 */
public class RequestActorsChain<INPUT, OUTPUT> {

    @SuppressWarnings("unchecked") // Generic element
    private static final RequestActorsChain TAIL = new RequestActorsChain() {
        @Override
        public Object next(final Object i, final Context context) throws Exception {
            throw new IllegalStateException("Reached chain tail without handling the input request");
        }
    };

    /*
     * The actors chain element payload
     */
    @VisibleForTesting
    final Optional<RequestActor<INPUT, OUTPUT>> actor;

    /*
     * Next sibling elementin the actors chain
     */
    @VisibleForTesting
    final Optional<RequestActorsChain<INPUT, OUTPUT>> next;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <I, O> RequestActorsChain<I, O> tail() {
        return TAIL;
    }

    public static <I, O> RequestActorsChain<I, O> makeChain(final RequestActor<I, O>...chain) {
        return makeChain(Lists.newArrayList(checkNotNull(chain)));
    }

    public static <I, O> RequestActorsChain<I, O> makeChain(final Iterable<? extends RequestActor<I, O>> chain) {
        return makeChain(checkNotNull(chain).iterator());
    }

    @SuppressWarnings("unchecked") // TAIL is generic
    public static <I, O> RequestActorsChain<I, O> makeChain(final Iterator<? extends RequestActor<I, O>> chain) {
        checkNotNull(chain);

        RequestActor<I, O> actor = null;

        while (chain.hasNext()) {
            actor = chain.next();
            if (null != actor) {
                break;
            }
        }

        if (null != actor) {
            return makeChain(chain).withPrevious(actor);
        }

        return TAIL;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked") // TAIL is generic
    public RequestActorsChain() {
        this.actor = Optional.absent();
        this.next = Optional.absent();
    }

    @SuppressWarnings("unchecked") // TAIL is generic
    public RequestActorsChain(final RequestActor<INPUT, OUTPUT> actor) {
        this.actor = Optional.of(checkNotNull(actor));
        this.next = Optional.of((RequestActorsChain<INPUT, OUTPUT>) TAIL);
    }

    public RequestActorsChain(final RequestActor<INPUT, OUTPUT> actor, final RequestActorsChain<INPUT, OUTPUT> next) {
        this.actor = Optional.of(checkNotNull(actor));
        this.next = Optional.of(checkNotNull(next));
    }

    // ------------------------------------------------------------------------

    public OUTPUT next(final INPUT input, final Context context) throws Exception {
        if (!actor.isPresent()) {
            throw new NoSuchElementException("Actors chain has not more elements !");
        }
        return actor.get().process(input, context, next.get());
    }

    public RequestActorsChain<INPUT, OUTPUT> withPrevious(RequestActor<INPUT, OUTPUT> previous) {
        return new RequestActorsChain<>(previous, this);
    }

}
