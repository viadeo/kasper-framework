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
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    private static class TestConcept extends Concept {
        LinkedConcept<TestConcept2> linkedTo;
    }

    @XKasperUnregistered
    private static class TestConcept2 extends Concept { }

    @XKasperUnregistered
    private static class TestRelation extends Relation { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithConcept() {
        // Given
        final EntityResolver resolver = new EntityResolver();
        final ConceptResolver conceptResolver = mock(ConceptResolver.class);

        resolver.setConceptResolver(conceptResolver);

        when(conceptResolver.getDomainClass(TestConcept.class))
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain = resolver.getDomainClass(TestConcept.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(conceptResolver, times(1)).getDomainClass(TestConcept.class);
        verifyNoMoreInteractions(conceptResolver);
    }

    @Test
    public void testGetDomainWithRelation() {
        // Given
        final EntityResolver resolver = new EntityResolver();
        final RelationResolver relationResolver = mock(RelationResolver.class);

        resolver.setRelationResolver(relationResolver);

        when( relationResolver.getDomainClass(TestRelation.class) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain = resolver.getDomainClass(TestRelation.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(relationResolver, times(1)).getDomainClass(TestRelation.class);
        verifyNoMoreInteractions(relationResolver);
    }

    @Test
    public void testGetComponentConcepts() {
        // Given
        final EntityResolver resolver = new EntityResolver();

        // When
        final List<Class<? extends Concept>> links =
                resolver.getComponentConcepts(TestConcept.class);

        // Then
        assertEquals(1, links.size());
        assertEquals(TestConcept2.class, links.get(0));
    }

}
