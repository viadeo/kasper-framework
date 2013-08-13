// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.ddd.specification;

import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.specification.ISpecification;
import com.viadeo.kasper.ddd.specification.SpecificationErrorMessage;
import com.viadeo.kasper.ddd.specification.impl.DefaultSpecificationErrorMessage;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
        final ISpecification isNotTest = isTest.not();

        // Then
        assertTrue(isNotTest.isSatisfiedBy("xyz"));
        assertFalse(isNotTest.isSatisfiedBy("test"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testFailingSpecification() {
        // Given
        final SpecificationErrorMessage message = new DefaultSpecificationErrorMessage();

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
        final SpecificationErrorMessage message = new DefaultSpecificationErrorMessage();
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
        final SpecificationErrorMessage message = new DefaultSpecificationErrorMessage();
        final ISpecification<String> failingSpecWithError = new SpecificationFailAnnotatedError();

        // When
        failingSpecWithError.isSatisfiedBy("xyz", message);

        // Then
        final Optional<String> strMessage = message.getMessage();
        assertTrue(strMessage.isPresent());
        assertEquals("it has failed for value xyz", strMessage.get());
    }

}
