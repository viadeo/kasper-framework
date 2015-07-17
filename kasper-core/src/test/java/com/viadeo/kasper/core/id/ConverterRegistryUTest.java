// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.viadeo.kasper.api.id.ID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConverterRegistryUTest {

    @Test
    public void register_withResolver_throwException() throws Exception {
        // Given
        ConverterRegistry repository = new ConverterRegistry();

        Converter resolver = new AbstractSimpleConverter("viadeo", TestFormats.ID, TestFormats.UUID) {
            @Override
            public ID convert(ID id) {
                return null;
            }
        };

        // When
        repository.register(resolver);

        // Then
        assertEquals(0, repository.getConvertersByFormats("viadeo").get(TestFormats.ID).size());
        assertEquals(1, repository.getConvertersByFormats("viadeo").get(TestFormats.UUID).size());
        assertEquals(resolver, repository.getConvertersByFormats("viadeo").get(TestFormats.UUID).iterator().next());

    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsResolver_throwException() throws Exception {
        new ConverterRegistry().register(null);
    }
}
