// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class KasperParanamerUTest {

    @Test
    public void isImmutable_witString_returnTrue() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        final boolean acceptable = kasperParanamer.isImmutable(String.class);

        // Then
        assertTrue(acceptable);
    }

    @Test
    public void isImmutable_withDate_returnFalse() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        final boolean acceptable = kasperParanamer.isImmutable(Date.class);

        // Then
        assertFalse(acceptable);
    }

    @Test(expected = NullPointerException.class)
    public void isImmutable_witNull_throwException() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        kasperParanamer.isImmutable(null);

        // Then throws an exception
    }

    @Test
    public void extractParameterNames_fromImmutableObject_usingCorrectlyJsonProperty_returnNames() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();
        final Class<KasperImmutabilityModuleITest.ImmutableObjectUsingJacksonAnnotation> declaringClass =
                KasperImmutabilityModuleITest.ImmutableObjectUsingJacksonAnnotation.class;

        // When
        final String[] strings = kasperParanamer.extractParameterNames(declaringClass.getConstructors()[0]);

        // Then
        assertArrayEquals(new String[]{"label", "value"}, strings);
    }

    @Test(expected = RuntimeException.class)
    public void extractParameterNames_fromImmutableObject_usingBadlyJsonProperty_returnNames() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();
        final Class<KasperImmutabilityModuleITest.ImmutableObjectUsingBadlyJacksonAnnotation> declaringClass =
                KasperImmutabilityModuleITest.ImmutableObjectUsingBadlyJacksonAnnotation.class;

        // When
        kasperParanamer.extractParameterNames(declaringClass.getConstructors()[0]);

        // Then throw exception
    }

    @Test(expected = NullPointerException.class)
    public void extractParameterNames_witNull_throwException() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        kasperParanamer.extractParameterNames(null);

        // Then throws an exception
    }
}
