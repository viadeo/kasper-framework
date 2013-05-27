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
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.NullSafeTypeAdapter;
import com.viadeo.kasper.query.exposition.QueryBuilder;
import com.viadeo.kasper.query.exposition.QueryParser;

public class KasperClientBuilderTest {

    @Test public void testCustomTypeAdapterOverrideDefault() {
        // Given
        final ITypeAdapter<Date> expected = new ITypeAdapter<Date>() {
            public void adapt(final Date value, final QueryBuilder builder) {
                // Empty
            }
            
            @Override
            public Date adapt(QueryParser parser) {
            	return null;
            }
        };
        
        // When
        final ITypeAdapter<Date> actual = new KasperClientBuilder().use(expected).create().queryFactory.create(TypeToken.of(Date.class));
        
        // Then
        assertEquals(expected, ((NullSafeTypeAdapter<Date>) actual).unwrap());
    }
}
