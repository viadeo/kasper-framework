/*
 * Copyright 2013 Viadeo.com
 */

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
        ITypeAdapter<Date> expected = new ITypeAdapter<Date>() {
            public void adapt(Date value, QueryBuilder builder) {
            }
        };
        
        // When
        ITypeAdapter<Date> actual = new KasperClientBuilder().use(expected).create().queryFactory.create(TypeToken.of(Date.class));
        
        // Then
        assertEquals(expected, actual);
    }
}
