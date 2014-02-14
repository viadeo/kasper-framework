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

    private static class MyParentComplexType {}

    private static class MyComplexType extends MyParentComplexType {}

    private static class BoundedTypeParameterBeanAdaptor<T extends MyParentComplexType> implements BeanAdapter<T> {
        @Override
        public void adapt(T value, QueryBuilder builder, BeanProperty property) throws Exception { }
        @Override
        public T adapt(QueryParser parser, BeanProperty property) throws Exception {
            return null;
        }
    }

    private static class ParameterizedBeanAdapter implements BeanAdapter<MyComplexType> {
        @Override
        public void adapt(MyComplexType value, QueryBuilder builder, BeanProperty property) throws Exception { }
        @Override
        public MyComplexType adapt(QueryParser parser, BeanProperty property) throws Exception {
            return null;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testTypeAdapterServiceLoading() {
        for (final TypeAdapter adapter : new QueryFactoryBuilder().loadServices(TypeAdapter.class)) {
            if (MyTestAdapter.class.equals(adapter.getClass())) {
                return;
            }
        }
        fail();
    }

    @Test
    public void use_withParameterizedBeanAdapter_shouldRegisterParameterizedTypeAsKey() throws Exception {
        // Given
        final QueryFactoryBuilder queryFactoryBuilder = new QueryFactoryBuilder();
        // When
        queryFactoryBuilder.use(new ParameterizedBeanAdapter());
        // Then
        final Map<Type, BeanAdapter> beanAdapters = getRegisteredBeanAdapters(queryFactoryBuilder);
        assertTrue("Parameterized BeanAdapter not registered correctly", beanAdapters.containsKey(MyComplexType.class));
    }

    @Test
    public void use_withBoundedTypeParameterBeanAdaptor_shouldRegisterUpperBoundAsKey() throws Exception {
        // Given
        final QueryFactoryBuilder queryFactoryBuilder = new QueryFactoryBuilder();
        // When
        queryFactoryBuilder.use(new BoundedTypeParameterBeanAdaptor());
        // Then
        final Map<Type, BeanAdapter> beanAdapters = getRegisteredBeanAdapters(queryFactoryBuilder);
        assertTrue("BeanAdapter with bounded type parameter not registered correctly", beanAdapters.containsKey(MyParentComplexType.class));
    }

    // ------------------------------------------------------------------------

    private Map<Type, BeanAdapter> getRegisteredBeanAdapters(final QueryFactoryBuilder queryFactoryBuilder) throws Exception {
        final Field beanAdaptersField = queryFactoryBuilder.getClass().getDeclaredField("beanAdapters");
        beanAdaptersField.setAccessible(true);
        return (Map<Type, BeanAdapter>)beanAdaptersField.get(queryFactoryBuilder);
    }

}
