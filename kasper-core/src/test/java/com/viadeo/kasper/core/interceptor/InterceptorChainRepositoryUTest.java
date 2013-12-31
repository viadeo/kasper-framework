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

public class InterceptorChainRepositoryUTest {

    private InterceptorChainRepository<Object, Object> chainRepository;

    @Before
    public void setUp() {
        chainRepository = new InterceptorChainRepository<>();
    }

    @Test(expected = NullPointerException.class)
    public void get_withNullAsKey_shouldThrownException() {
        // Given
        Class key = null;

        // When
        chainRepository.get(key);

        // Then throws an exception
    }

    @Test
    public void get_withUnknownKey_shouldReturnAbsent() {
        // Given
        Class key = String.class;

        // When
        Optional chainOptional = chainRepository.get(key);

        // Then
        assertNotNull(chainOptional);
        assertFalse(chainOptional.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void get_afterCreate_shouldReturnChain() {
        // Given
        Class key = String.class;
        Interceptor<Object, Object> tail = mock(Interceptor.class);
        chainRepository.create(key, tail);

        // When
        Optional<InterceptorChain<Object, Object>> chainOptional = chainRepository.get(key);

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());
        assertEquals(tail, chainOptional.get().actor.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create_withoutRegisteredInterceptorFactory_shouldCreateChain() {
        // Given
        Class key = String.class;
        Interceptor<Object, Object> tail = mock(Interceptor.class);

        // When
        Optional<InterceptorChain<Object, Object>> chainOptional = chainRepository.create(key, tail);

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create_withRegisteredInterceptorFactory_shouldCreateChain() {
        // Given
        Class key = String.class;

        Interceptor<Object, Object> head = mock(Interceptor.class);
        Interceptor<Object, Object> tail = mock(Interceptor.class);

        InterceptorFactory<Object, Object> headFactory = mock(InterceptorFactory.class);
        when(headFactory.create(any(TypeToken.class))).thenReturn(Optional.of(InterceptorChain.makeChain(head)));

        chainRepository.register(headFactory);

        // When
        Optional<InterceptorChain<Object, Object>> chainOptional = chainRepository.create(key, tail);

        // Then
        assertNotNull(chainOptional);
        assertTrue(chainOptional.isPresent());

        assertEquals(head, chainOptional.get().actor.get());
        assertEquals(tail, chainOptional.get().next.get().actor.get());
    }
}
