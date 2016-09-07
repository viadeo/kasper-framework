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
package com.viadeo.kasper.core.component.command.aggregate.ddd.specification;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpecificationTest {

    final private ISpecification<String> isTest = new SpecificationStringIs("test");
    final private ISpecification<String> containsA = new SpecificationStringContains("a");
    final private ISpecification<String> containsB = new SpecificationStringContains("b");

    // ------------------------------------------------------------------------

    @Test
    public void testSpecificationSimple() {
        assertTrue(isTest.isSatisfiedBy("test"));
        assertFalse(isTest.isSatisfiedBy("abc"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSpecificationAnd() {
        // Given
        final ISpecification<String> isTestAndContainsA = isTest.and(containsA);
        final ISpecification<String> containsAAndIsTest = containsA.and(isTest);
        final ISpecification<String> containsAAndContainsB = containsA.and(containsB);
        final ISpecification<String> containsBAndContainsA = containsB.and(containsA);

        // Then
        assertFalse(isTestAndContainsA.isSatisfiedBy("test"));
        assertFalse(isTestAndContainsA.isSatisfiedBy("abc"));

        assertFalse(containsAAndIsTest.isSatisfiedBy("test"));
        assertFalse(containsAAndIsTest.isSatisfiedBy("xyz"));

        assertTrue(containsAAndContainsB.isSatisfiedBy("abc"));
        assertTrue(containsBAndContainsA.isSatisfiedBy("abc"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSpecificationOrPositive() {
        // Given
        final ISpecification<String> containsAOrContainsB = containsA.or(containsB);
        final ISpecification<String> containsBOrContainsA = containsB.or(containsA);

        // Then
        assertTrue(containsAOrContainsB.isSatisfiedBy("abc"));
        assertTrue(containsAOrContainsB.isSatisfiedBy("ab"));
        assertTrue(containsAOrContainsB.isSatisfiedBy("bc"));
        assertFalse(containsAOrContainsB.isSatisfiedBy("xyz"));

        assertTrue(containsBOrContainsA.isSatisfiedBy("abc"));
        assertTrue(containsBOrContainsA.isSatisfiedBy("ab"));
        assertTrue(containsBOrContainsA.isSatisfiedBy("bc"));
        assertFalse(containsBOrContainsA.isSatisfiedBy("xyz"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSpecificationNot() {
        // Given
        final ISpecification<String> isNotTest = isTest.not();

        // Then
        assertTrue(isNotTest.isSatisfiedBy("xyz"));
        assertFalse(isNotTest.isSatisfiedBy("test"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testFailingSpecification() {
        // Given
        final SpecificationErrorMessage message = new SpecificationErrorMessage();

        // When
        isTest.isSatisfiedBy("xyz", message);

        // Then
        final Optional<String> strMessage = message.getMessage();
        assertTrue(strMessage.isPresent());
        assertEquals("SpecificationStringIs specification was not met for value xyz", strMessage.get());
    }

     // ------------------------------------------------------------------------

    @Test
    public void testFailingSpecificationWithDescription() {
        // Given
        final SpecificationErrorMessage message = new SpecificationErrorMessage();
        final ISpecification<String> failingSpecWithDescription = new SpecificationFailError();

        // When
        failingSpecWithDescription.isSatisfiedBy("xyz", message);

        // Then
        final Optional<String> strMessage = message.getMessage();
        assertTrue(strMessage.isPresent());
        assertEquals("Specification not met : this specification should always fail for value xyz", strMessage.get());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testFailingSpecificationWithError() {
        // Given
        final SpecificationErrorMessage message = new SpecificationErrorMessage();
        final ISpecification<String> failingSpecWithError = new SpecificationFailAnnotatedError();

        // When
        failingSpecWithError.isSatisfiedBy("xyz", message);

        // Then
        final Optional<String> strMessage = message.getMessage();
        assertTrue(strMessage.isPresent());
        assertEquals("it has failed for value xyz", strMessage.get());
    }

}
