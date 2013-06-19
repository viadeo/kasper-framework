// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.NullSafeTypeAdapter;
import com.viadeo.kasper.query.exposition.QueryBuilder;
import com.viadeo.kasper.query.exposition.QueryParser;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class KasperClientBuilderTest {

    @Test public void testCustomTypeAdapterOverrideDefault() {
        // Given
        final TypeAdapter<Date> expected = new TypeAdapter<Date>() {
            public void adapt(final Date value, final QueryBuilder builder) {
                // Empty
            }
            
            @Override
            public Date adapt(final QueryParser parser) {
            	return null;
            }
        };
        
        // When
        final TypeAdapter<Date> actual = new KasperClientBuilder().use(expected).create()
                                                .queryFactory.create(TypeToken.of(Date.class));
        
        // Then
        assertEquals(expected, ((NullSafeTypeAdapter<Date>) actual).unwrap());
    }
}
