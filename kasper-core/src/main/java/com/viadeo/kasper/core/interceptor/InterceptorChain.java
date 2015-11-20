// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import org.slf4j.MDC;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * An element of an actors chain
 */
public class InterceptorChain<INPUT, OUTPUT> {

    @SuppressWarnings("unchecked") // Generic element
    private static final InterceptorChain TAIL = new InterceptorChain() {
        @Override
        public Object next(final Object i, final Context context) throws Exception {
            throw new IllegalStateException("Reached chain tail without handling the input request");
        }
    };

    @SuppressWarnings("unchecked")
    public static <I, O> InterceptorChain<I, O> tail() {
        return TAIL;
    }

    // ------------------------------------------------------------------------

    @SafeVarargs
    public static <I, O> InterceptorChain<I, O> makeChain(final Interceptor<I, O>...chain) {
        return makeChain(Lists.newArrayList(checkNotNull(chain)));
    }

    public static <I, O> InterceptorChain<I, O> makeChain(final Iterable<? extends Interceptor<I, O>> chain) {
        return makeChain(checkNotNull(chain).iterator());
    }

    @SuppressWarnings("unchecked") // TAIL is generic
    public static <I, O> InterceptorChain<I, O> makeChain(final Iterator<? extends Interceptor<I, O>> chain) {
        checkNotNull(chain);

        Interceptor<I, O> actor = null;

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

    /*
     * The actors chain element result
     */
    @VisibleForTesting
    public final Optional<Interceptor<INPUT, OUTPUT>> actor;

    /*
     * Next sibling elementin the actors chain
     */
    @VisibleForTesting
    public final Optional<InterceptorChain<INPUT, OUTPUT>> next;

    // ------------------------------------------------------------------------

    public InterceptorChain() {
        this.actor = Optional.absent();
        this.next = Optional.absent();
    }

    @SuppressWarnings("unchecked") // TAIL is generic
    public InterceptorChain(final Interceptor<INPUT, OUTPUT> actor) {
        this.actor = Optional.of(checkNotNull(actor));
        this.next = Optional.of((InterceptorChain<INPUT, OUTPUT>) TAIL);
    }

    public InterceptorChain(final Interceptor<INPUT, OUTPUT> actor, final InterceptorChain<INPUT, OUTPUT> next) {
        this.actor = Optional.of(checkNotNull(actor));
        this.next = Optional.of(checkNotNull(next));
    }

    // ------------------------------------------------------------------------

    public OUTPUT next(final INPUT input, final Context context) throws Exception {
        if ( ! actor.isPresent()) {
            throw new NoSuchElementException("Actors chain has not more elements !");
        }

        if ( ! CurrentContext.value().isPresent() || (CurrentContext.value().isPresent() && ! CurrentContext.value().get().equals(context)) ) {
            MDC.setContextMap(context.asMap());
        }

        return actor.get().process(input, context, next.get());
    }

    /**
     * @return the last interceptor of this chain
     */
    public Optional<Interceptor<INPUT, OUTPUT>> last() {
        if (next.isPresent() && next.get().actor.isPresent()) {
            return next.get().last();
        } else {
            return actor;
        }
    }

    public InterceptorChain<INPUT, OUTPUT> withPrevious(final Interceptor<INPUT, OUTPUT> previous) {
        return new InterceptorChain<>(previous, this);
    }

    public InterceptorChain<INPUT, OUTPUT> withNextChain(final InterceptorChain<INPUT, OUTPUT> chain) {
        return new InterceptorChain<>(this.actor.get(), chain);
    }

}
