// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SerDeserTests {

    // -- test beans ----------------------------------------------------------

    public static class SimpleBean {

        private String field;

        public SimpleBean() { }

        public SimpleBean(final String field) {
            this.field = field;
        }

        public void setField(final String field) {
            this.field = field;
        }

    }

    public static class ImmutableBean {
        private final String field;

        /* Private constructor for ser/deser */
        private ImmutableBean() { field = null; }

        public ImmutableBean(final String field) {
            this.field = field;
        }
    }

    public static class NoSettersBean {
        String field;

        public NoSettersBean() { field = null; }

        public NoSettersBean(final String field) {
            this.field = field;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_SimpleBean() throws IOException {
        // Given
        final SimpleBean bean = new SimpleBean("test");

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(bean);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final SimpleBean actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json), SimpleBean.class);

        // Then
        assertEquals(actualResult.field, bean.field);
    }

    @Test
    public void test_ImmutableBean() throws IOException {
        // Given
        final ImmutableBean bean = new ImmutableBean("test");

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(bean);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final ImmutableBean actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json), ImmutableBean.class);

        // Then
        assertEquals(actualResult.field, bean.field);
    }

    @Test
    public void test_NoSettersBean() throws IOException {
        // Given
        final NoSettersBean bean = new NoSettersBean("test");

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(bean);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final NoSettersBean actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json), NoSettersBean.class);

        // Then
        assertEquals(actualResult.field, bean.field);
    }


}
