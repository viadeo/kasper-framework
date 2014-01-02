// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompositeInterceptorFactoryUTest {

    @Test(expected = NullPointerException.class)
    public void create_withNullAsTypeToken_shouldThrownException() {
        // Given
        final CompositeInterceptorFactory<Object, Object> compositeInterceptorFactory = new CompositeInterceptorFactory<>(
                Lists.<InterceptorFactory<Object,Object>>newArrayList()
        );

        // When
        compositeInterceptorFactory.create(null);

        // Then throws exception
    }

    @Test
    public void create_withNoRegisteredInterceptorFactory_shouldReturnedAbsent() {
        // Given
        final CompositeInterceptorFactory<Object, Object> compositeInterceptorFactory = new CompositeInterceptorFactory<>(
                Lists.<InterceptorFactory<Object,Object>>newArrayList()
        );

        // When
        final Optional<InterceptorChain<Object,Object>> interceptorChainOptional =
                compositeInterceptorFactory.create(TypeToken.of(String.class));

        // Then
        assertNotNull(interceptorChainOptional);
        assertFalse(interceptorChainOptional.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create_withAcceptableType_withInterceptorFactoryReturningAbsent_shouldReturnedTail() {
        // Given
        final InterceptorFactory<Object, Object> interceptorFactory = mock(InterceptorFactory.class);
        when(interceptorFactory.create(any(TypeToken.class))).thenReturn(Optional.<InterceptorChain<Object, Object>>absent());

        final CompositeInterceptorFactory<Object, Object> compositeInterceptorFactory = new CompositeInterceptorFactory<>(
                Lists.newArrayList(interceptorFactory)
        );

        // When
        final Optional<InterceptorChain<Object,Object>> interceptorChainOptional =
                compositeInterceptorFactory.create(TypeToken.of(String.class));

        // Then
        assertNotNull(interceptorChainOptional);
        assertFalse(interceptorChainOptional.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create_withAcceptableType_withInterceptorFactoryReturningChain_shouldReturnedFullChain() {
        // Given
        final InterceptorChain<Object, Object> chainA = mock(InterceptorChain.class);

        final InterceptorFactory<Object, Object> interceptorFactory = mock(InterceptorFactory.class);
        when(interceptorFactory.create(any(TypeToken.class))).thenReturn(Optional.of(chainA));


        final CompositeInterceptorFactory<Object, Object> compositeInterceptorFactory = new CompositeInterceptorFactory<>(
                Lists.newArrayList(interceptorFactory)
        );

        // When
        final Optional<InterceptorChain<Object,Object>> interceptorChainOptional =
                compositeInterceptorFactory.create(TypeToken.of(String.class));

        // Then
        assertNotNull(interceptorChainOptional);
        assertTrue(interceptorChainOptional.isPresent());
        assertEquals(chainA, interceptorChainOptional.get());
    }

}
