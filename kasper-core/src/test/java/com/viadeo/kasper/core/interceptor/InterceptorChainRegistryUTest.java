// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InterceptorChainRegistryUTest {

    private InterceptorChainRegistry<Object, Object> chainRegistry;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Interceptor<Object, Object> createMockedInterceptor(final String name) {
        final Interceptor<Object, Object> interceptor = mock(Interceptor.class);
        when(interceptor.toString()).thenReturn(name);
        return interceptor;
    }

    private static class MockInterceptorFactory implements InterceptorFactory<Object, Object> {
        private final InterceptorChain<Object, Object> interceptorChain;

        public MockInterceptorFactory(final Interceptor<Object, Object> interceptor) {
            this.interceptorChain = InterceptorChain.makeChain(interceptor);
        }

        @Override
        public Optional<InterceptorChain<Object, Object>> create(final TypeToken<?> type) {
            return Optional.of(interceptorChain);
        }

        @Override
        public boolean accept(final TypeToken<?> type) {
            return true;
        }
    }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        chainRegistry = new InterceptorChainRegistry<>();
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void get_withNullAsKey_shouldThrownException() {
        // Given
        final Class key = null;

        // When
        chainRegistry.get(key);

        // Then throws an exception
    }

    @Test
    public void get_withUnknownKey_shouldReturnAbsent() {
        // Given
        final Class key = String.class;

        // When
        final Optional chainOptional = chainRegistry.get(key);

        // Then
        assertNotNull(chainOptional);
        assertFalse(chainOptional.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void get_afterCreate_shouldReturnChain() {
        // Given
        final Class key = String.class;
        final Interceptor<Object, Object> tail = createMockedInterceptor("tail");
        chainRegistry.create(key, new MockInterceptorFactory(tail));

        // When
        final Optional<InterceptorChain<Object, Object>> chainOptional = chainRegistry.get(key);

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());
        assertEquals(tail, chainOptional.get().actor.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create_withoutRegisteredInterceptorFactory_shouldCreateChain() {
        // Given
        final Class key = String.class;
        final Interceptor<Object, Object> tail = createMockedInterceptor("tail");

        // When
        final Optional<InterceptorChain<Object, Object>> chainOptional = chainRegistry.create(key, new MockInterceptorFactory(tail));

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create_withRegisteredInterceptorFactory_shouldCreateChain() {
        // Given
        final Class key = String.class;

        final Interceptor<Object, Object> head = createMockedInterceptor("head");
        final Interceptor<Object, Object> tail = createMockedInterceptor("tail");

        final InterceptorFactory<Object, Object> headFactory = mock(InterceptorFactory.class);
        when(headFactory.create(any(TypeToken.class))).thenReturn(Optional.of(InterceptorChain.makeChain(head)));

        chainRegistry.register(headFactory);

        // When
        final Optional<InterceptorChain<Object, Object>> chainOptional = chainRegistry.create(key, new MockInterceptorFactory(tail));

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());

        assertEquals(head, chainOptional.get().actor.get());
        assertEquals(tail, chainOptional.get().next.get().actor.get());
    }

    @Test
    public void create_withMinimumTwoRegisteredInterceptorFactory_shouldCreateChain() {
        // Given
        final Class key = String.class;

        final Interceptor<Object, Object> elem1 = createMockedInterceptor("elem1");
        final Interceptor<Object, Object> elem2 = createMockedInterceptor("elem2");
        final Interceptor<Object, Object> tail = createMockedInterceptor("tail");

        chainRegistry.register(new MockInterceptorFactory(elem1));
        chainRegistry.register(new MockInterceptorFactory(elem2));

        // When
        final Optional<InterceptorChain<Object, Object>> chainOptional = chainRegistry.create(key, new MockInterceptorFactory(tail));

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());

        InterceptorChain<Object, Object> chain1 = chainOptional.get();
        assertEquals(elem1, chain1.actor.get());

        InterceptorChain<Object, Object> chain2 = chain1.next.get();
        assertEquals(elem2, chain2.actor.get());

        InterceptorChain<Object, Object> chain3 = chain2.next.get();
        assertEquals(tail, chain3.actor.get());
        assertEquals(InterceptorChain.tail(), chain3.next.get());
    }

}