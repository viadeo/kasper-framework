// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.interceptor.QueryInterceptor;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryCache;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryFilter;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.interceptor.CacheInterceptor;
import com.viadeo.kasper.cqrs.query.interceptor.CacheInterceptorFactory;
import com.viadeo.kasper.cqrs.query.interceptor.QueryFilterInterceptorFactory;
import com.viadeo.kasper.cqrs.query.interceptor.QueryHandlerInterceptor;
import com.viadeo.kasper.ddd.Domain;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.slf4j.MDC;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;

public class KasperQueryGatewayUTest {

    private final KasperQueryGateway queryGateway;
    private final DefaultQueryHandlersLocator queryHandlersLocator;
    private final InterceptorChainRegistry interceptorChainRegistry;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @XKasperQueryHandler(domain = Domain.class)
    private static class QueryHandlerForTest extends QueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class)
    @XKasperQueryFilter(value = {InterceptorA.class})
    private static class QueryHandlerWithFiltersForTest extends QueryHandler<Query, QueryResult> { }

    @XKasperQueryHandler(domain = Domain.class, cache = @XKasperQueryCache(enabled = true))
    private static class QueryHandlerWithCacheForTest extends QueryHandler<Query, QueryResult> { }

    public static class InterceptorA implements QueryInterceptor<Query, QueryResult> {
        @Override
        public QueryResponse<QueryResult> process(Query query, Context context, InterceptorChain<Query, QueryResponse<QueryResult>> chain) throws Exception {
            return chain.next(query, context);
        }
    }

    // ------------------------------------------------------------------------

    public KasperQueryGatewayUTest() {
        queryHandlersLocator = mock(DefaultQueryHandlersLocator.class);
        interceptorChainRegistry = mock(InterceptorChainRegistry.class);
        queryGateway = new KasperQueryGateway(queryHandlersLocator, interceptorChainRegistry);
    }

    @After
    public void clean() {
        reset(queryHandlersLocator, interceptorChainRegistry);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void enrichMdcAndMdcContextMap_withRegisteredQueryWithTags_shouldAddItsTagsToTheContextBeforeEnrichingTheMdcContextMap() {
        // Given
        Query query = new TestQuery();
        when(queryHandlersLocator.getHandlerFromQueryClass(query.getClass()))
                .thenReturn(Optional.<QueryHandler<Query, QueryResult>>of((QueryHandler) new TestQueryHandler_WithSeveralTags()));
        Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);

        Context context = mock(Context.class);
        Set<String> initialSetOfTags = Sets.newHashSet();
        when(context.getTags())
                .thenReturn(initialSetOfTags);

        when(context.setTags(expectedTags))
                .thenReturn(context);

        Map<String, String> initialContextMap = ImmutableMap.of("foo", "bar");
        MDC.setContextMap(initialContextMap);

        Map<String, String> extendedContextMap = ImmutableMap.of("baz", "qux");
        when(context.asMap(initialContextMap))
                .thenReturn(extendedContextMap);


        // When
        queryGateway.enrichContextAndMdcContextMap(query, context);

        // Then
        InOrder inOrder = inOrder(context);
        inOrder.verify(context)
                .getTags();
        inOrder.verify(context)
                .setTags(expectedTags);
        inOrder.verify(context)
                .asMap(initialContextMap);
        inOrder.verifyNoMoreInteractions();

        assertEquals(extendedContextMap, MDC.getCopyOfContextMap());
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerClass_withNull_shouldThrowNPE() {
        // Given
        Query query = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        queryGateway.getHandlerClass(query);
    }

    @Test
    public void getHandlerClass_withUnregisteredQuery_shouldReturnNull() {
        // Given
        Query query = new TestQuery();
        when(queryHandlersLocator.getHandlerFromQueryClass(query.getClass()))
                .thenReturn(Optional.<QueryHandler<Query, QueryResult>>absent());

        // When
        Class<? extends QueryHandler> handlerClass = queryGateway.getHandlerClass(query);

        // Then
        assertNull(handlerClass);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getHandlerClass_withRegisteredQuery_shouldReturnTheHandlersClass() {
        // Given
        Query query = new TestQuery();
        QueryHandler registeredHandler = new TestQueryHandler_WithSeveralTags();
        when(queryHandlersLocator.getHandlerFromQueryClass(query.getClass()))
                .thenReturn(Optional.<QueryHandler<Query, QueryResult>>of(registeredHandler));

        // When
        Class<? extends QueryHandler> handlerClass = queryGateway.getHandlerClass(query);

        // Then
        assertEquals(registeredHandler.getClass(), handlerClass);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void addHandlerTagsToContext_withNullContext_shouldThrowNPE() {
        // Given
        Context context = null;
        Class<? extends QueryHandler> handlerClass = TestQueryHandler_WithSeveralTags.class;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        KasperQueryGateway.addHandlerTagsToContext(handlerClass, context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addHandlerTagsToContext_withHandlerWithSeveralTags_shouldAddThemToExistingTagsInContext() {
        // Given
        Context context = mock(Context.class);

        Set<String> initialSetOfTags = Sets.newHashSet("preexisting tag");
        when(context.getTags())
                .thenReturn(initialSetOfTags);

        when(context.setTags(anySet()))
                .thenReturn(context);

        Class<? extends QueryHandler> handlerClass = TestQueryHandler_WithSeveralTags.class;
        Set<String> expectedTags = newHashSet("preexisting tag", TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);

        // When
        KasperQueryGateway.addHandlerTagsToContext(handlerClass, context);

        // Then
        InOrder inOrder = inOrder(context);
        inOrder.verify(context)
                .getTags();
        inOrder.verify(context)
                .setTags(expectedTags);
        inOrder.verifyNoMoreInteractions();
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withNull_shouldThrowNPE() {
        // Given
        Class<? extends QueryHandler> handlerClass = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        KasperQueryGateway.getHandlerTags(handlerClass);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithNoTags_shouldReturnEmpty() {
        // Given
        Class<? extends QueryHandler> handlerClass = TestQueryHandler_WithNoTags.class;

        // When
        Set<String> tags = KasperQueryGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithOneTag_shouldReturnTheSingletonSet() {
        // Given
        Class<? extends QueryHandler> handlerClass = TestQueryHandler_WithOneTag.class;

        // When
        Set<String> tags = KasperQueryGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithSeveralTags_shouldReturnTheSet() {
        // Given
        Class<? extends QueryHandler> handlerClass = TestQueryHandler_WithSeveralTags.class;

        // When
        Set<String> tags = KasperQueryGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2), tags);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithoutAnnotations_shouldReturnEmpty() {
        // Given
        Class<? extends QueryHandler> handlerClass = TestQueryHandler_WithoutAnnotation.class;

        // When
        Set<String> tags = KasperQueryGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void register_withNullAsQueryHandler_shouldThrownException() {
        // Given
        final QueryHandler queryHandler = null;

        // When
        queryGateway.register(queryHandler);

        // Then throws an exception
    }

    @Test
    public void register_withQueryHandler_shouldBeRegistered() {
        // Given
        final QueryHandler queryHandler = new QueryHandlerForTest();

        // When
        queryGateway.register(queryHandler);

        // Then
        verify(queryHandlersLocator).registerHandler(refEq("QueryHandlerForTest"), refEq(queryHandler), refEq(Domain.class));
        verifyNoMoreInteractions(queryHandlersLocator);

        assertEquals(queryGateway, queryHandler.getQueryGateway());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getInterceptorChain_withUnknownQuery_shouldReturnNoChain() {
        // Given
        when(interceptorChainRegistry.get(any(Class.class))).thenReturn(Optional.absent());
        when(queryHandlersLocator.getHandlerFromQueryClass(any(Class.class))).thenReturn(Optional.<QueryHandler<Query, QueryResult>>absent());


        // When
        final Optional<InterceptorChain<Query,QueryResponse<QueryResult>>> requestActorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertNotNull(requestActorChain);
        assertFalse(requestActorChain.isPresent());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_shouldBeOk() {
        // Given
        final QueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway();
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertTrue(interceptorChain.isPresent());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_withFilter_shouldBeOk() {
        // Given
        final QueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithFiltersForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway();
        queryGateway.register(new QueryFilterInterceptorFactory());
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertTrue(interceptorChain.isPresent());
        assertEquals(InterceptorA.class, interceptorChain.get().actor.get().getClass());
        assertEquals(QueryHandlerInterceptor.class, interceptorChain.get().next.get().actor.get().getClass());
    }

    @Test
    public void getInterceptorChain_withQueryHandler_withCache_shouldBeOk() {
        // Given
        final QueryHandler<Query,QueryResult> queryHandler = new QueryHandlerWithCacheForTest();

        final KasperQueryGateway queryGateway = new KasperQueryGateway();
        queryGateway.register(new CacheInterceptorFactory());
        queryGateway.register(queryHandler);

        // When
        final Optional<InterceptorChain<Query, QueryResponse<QueryResult>>> interceptorChain =
                queryGateway.getInterceptorChain(Query.class);

        // Then
        assertTrue(interceptorChain.isPresent());
        assertEquals(CacheInterceptor.class, interceptorChain.get().actor.get().getClass());
        assertEquals(QueryHandlerInterceptor.class, interceptorChain.get().next.get().actor.get().getClass());
    }
    // ------------------------------------------------------------------------

    private static final String TEST_COMMAND_TAG = "this-is-a-tag";

    private static final String TEST_COMMAND_TAG_2 = "this-is-another-tag";

    @XKasperUnregistered
    private static class TestDomain implements Domain {
    }

    @XKasperUnregistered
    private static class TestQuery implements Query {
    }

    @XKasperUnregistered
    private static class TestQueryResult implements QueryResult {
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class)
    private static class TestQueryHandler_WithNoTags extends QueryHandler<TestQuery, TestQueryResult> {
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class, tags = TEST_COMMAND_TAG)
    private static class TestQueryHandler_WithOneTag extends QueryHandler<TestQuery, TestQueryResult> {
    }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
    private static class TestQueryHandler_WithSeveralTags extends QueryHandler<TestQuery, TestQueryResult> {
    }

    @XKasperUnregistered
    private static class TestQueryHandler_WithoutAnnotation extends QueryHandler<TestQuery, TestQueryResult> {
    }

}
