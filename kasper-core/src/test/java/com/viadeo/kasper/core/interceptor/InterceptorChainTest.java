// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.api.domain.query.Query;
import com.viadeo.kasper.api.domain.query.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class InterceptorChainTest {

    static class DummyInterceptor implements Interceptor<Query, QueryResult> {
        @Override
        public QueryResult process(Query query, Context context, InterceptorChain chain) {
            return null;
        }
    }

    // ------------------------------------------------------------------------


    @Before
    public void setUp() throws Exception {
        MDC.clear();
        CurrentContext.clear();
    }

    @Test
    public void testIteratorToChain() {
        // Given
        final DummyInterceptor d1 = new DummyInterceptor();
        final DummyInterceptor d2 = new DummyInterceptor();
        final Iterable<DummyInterceptor> chain = Lists.newArrayList(null, d1, null, d2, null);

        // When
        final InterceptorChain<Query, QueryResult> actualChain = InterceptorChain.makeChain(chain);

        // Then
        InterceptorChain<Query, QueryResult> elt = actualChain;
        for (final DummyInterceptor e : Lists.newArrayList(d1, d2)) {
            assertEquals(e, elt.actor.get());
            elt = actualChain.next.get();
        }
    }

    @Test
    public void last_fromEmptyChain_shouldReturnAbsent(){
        // Given
        final InterceptorChain<Query, QueryResult> chain = new InterceptorChain<>();

        // When
        final Optional<Interceptor<Query,QueryResult>> optionalLastInterceptor = chain.last();

        // Then
        assertFalse(optionalLastInterceptor.isPresent());
    }

    @Test
    public void last_shouldReturnLastInterceptor(){
        // Given
        final DummyInterceptor d1 = new DummyInterceptor();
        final DummyInterceptor d2 = new DummyInterceptor();
        final InterceptorChain<Query, QueryResult> chain = InterceptorChain.makeChain(d1, d2);

        // When
        final Optional<Interceptor<Query,QueryResult>> optionalLastInterceptor = chain.last();

        // Then
        assertTrue(optionalLastInterceptor.isPresent());
        assertEquals(optionalLastInterceptor.get(), d2);
    }

    @Test
    public void next_withSameContext_isOk() throws Exception {
        // Given
        final Context context = Contexts.builder(UUID.randomUUID())
                .withUserCountry("FR")
                .withUserLang("fr")
                .addTags(Sets.newHashSet("a", "b"))
                .build();

        CurrentContext.set(context);
        MDC.setContextMap(context.asMap());

        final InterceptorChain<Query, QueryResult> chain = InterceptorChain.makeChain(new DummyInterceptor());

        // When
        chain.next(mock(Query.class), context);

        // Then
        Optional<Context> optionalCurrentContext = CurrentContext.value();
        assertTrue(optionalCurrentContext.isPresent());
        assertEquals(context, optionalCurrentContext.get());
        assertEquals(context.asMap(), MDC.getCopyOfContextMap());
    }

    @Test
    public void next_withoutCurrentContext_shouldSetContextAsCurrentAndSetMDC() throws Exception {
        // Given
        CurrentContext.clear();
        final Context context = Contexts.builder(UUID.randomUUID())
                .withUserCountry("FR")
                .withUserLang("fr")
                .addTags(Sets.newHashSet("a", "b"))
                .build();
        final InterceptorChain<Query, QueryResult> chain = InterceptorChain.makeChain(new DummyInterceptor());

        // When
        chain.next(mock(Query.class), context);

        // Then
        Optional<Context> optionalCurrentContext = CurrentContext.value();
        assertTrue(optionalCurrentContext.isPresent());
        assertEquals(context, optionalCurrentContext.get());
        assertEquals(context.asMap(), MDC.getCopyOfContextMap());
    }

    @Test
    public void next_withUpdatedContext_shouldSetContextAsCurrentAndSetMDC() throws Exception {
        // Given
        final Context context = Contexts.builder(UUID.randomUUID())
                .withUserCountry("FR")
                .withUserLang("fr")
                .addTags(Sets.newHashSet("a", "b"))
                .build();

        CurrentContext.set(context);

        final Context newContext = Contexts.newFrom(context)
                .withFunnelName("MyFunnelRocks")
                .addTags(Sets.newHashSet("c"))
                .with("myPropertyKey", "myPropertyValue")
                .build();

        final InterceptorChain<Query, QueryResult> chain = InterceptorChain.makeChain(new DummyInterceptor());

        // When
        chain.next(mock(Query.class), newContext);

        // Then
        Optional<Context> optionalCurrentContext = CurrentContext.value();
        assertTrue(optionalCurrentContext.isPresent());
        assertEquals(newContext, optionalCurrentContext.get());
        assertEquals(newContext.asMap(), MDC.getCopyOfContextMap());
    }

}
