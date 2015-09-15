// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.viadeo.kasper.api.id.Format;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestConverters {

    private TestConverters() {}

    public static Converter mockConverter(final String vendor, final Format source, final Format target) {
        Converter converter = mock(Converter.class);
        when(converter.getSource()).thenReturn(source);
        when(converter.getTarget()).thenReturn(target);
        when(converter.getVendor()).thenReturn(vendor);
        return converter;
    }
}
