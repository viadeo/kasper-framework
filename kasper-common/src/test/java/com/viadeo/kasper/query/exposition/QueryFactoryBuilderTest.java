// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.viadeo.kasper.query.exposition.query.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QueryFactoryBuilderTest {

    @Test
    public void testTypeAdapterServiceLoading() {
        for (final TypeAdapter adapter : new QueryFactoryBuilder().loadServices(TypeAdapter.class)) {
            if (MyTestAdapter.class.equals(adapter.getClass())) {
                return;
            }
        }
        fail();
    }

}