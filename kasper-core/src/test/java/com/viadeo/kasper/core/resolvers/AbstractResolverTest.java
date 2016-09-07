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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    private static class ImplementedAbstractResolver extends AbstractResolver<Object> {
        @Override
        public String getTypeName() { return "Object"; }
        @Override
        public String getLabel(Class<?> clazz) { return null; }
        @Override
        public String getDescription(Class<?> clazz) { return null; }
        @Override
        public Optional<Class<? extends Domain>> getDomainClass(final Class clazz) {
            return Optional.<Class<? extends Domain>>of(TestDomain.class);
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomain() {
        // Given
        final ImplementedAbstractResolver resolver = new ImplementedAbstractResolver();
        final DomainResolver domainResolver = mock(DomainResolver.class);

        resolver.setDomainResolver(domainResolver);

        when( domainResolver.getLabel(TestDomain.class) )
                .thenReturn( "Test" );

        // When
        final String label = resolver.getDomainLabel(TestDomain.class);

        // Then
        assertEquals("Test", label);
    }

}
