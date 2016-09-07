// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
