// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.client.lib.ITypeAdapter;
import com.viadeo.kasper.client.lib.QueryBuilder;

public class KasperClientBuilderTest {

    @Test public void testCustomTypeAdapterOverrideDefault() {
        // Given
        final ITypeAdapter<Date> expected = new ITypeAdapter<Date>() {
            public void adapt(final Date value, final QueryBuilder builder) {
                // Empty
            }
        };
        
        // When
        final ITypeAdapter<Date> actual = new KasperClientBuilder().use(expected).create().queryFactory.create(TypeToken.of(Date.class));
        
        // Then
        assertEquals(expected, actual);
    }
}
