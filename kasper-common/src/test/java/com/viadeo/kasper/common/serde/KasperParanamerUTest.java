// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.serde;

import org.junit.Test;

import java.util.Date;

import static com.viadeo.kasper.common.serde.ImmutabilityModule.KasperParanamer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
