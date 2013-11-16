// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SerDeserTests {

    // -- test beans ----------------------------------------------------------

    public static class SimpleBean {

        private KasperID field;

        public SimpleBean() { }

        public SimpleBean(final KasperID field) {
            this.field = field;
        }

        public void setField(final KasperID field) {
            this.field = field;
        }

        public KasperID getField() {
            return this.field;
        }

    }

    public static class ImmutableBean {
        private final KasperRelationID field;

        /* Private constructor for ser/deser */
        private ImmutableBean() { field = null; }

        public ImmutableBean(final KasperRelationID field) {
            this.field = field;
        }

        public KasperRelationID getField(){
            return this.field;
        }
    }

    public static class NoSettersBean {
        String field;

        public NoSettersBean() { field = null; }

        public NoSettersBean(final String field) {
            this.field = field;
        }

        public String getField() {
            return this.field;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_SimpleBean() throws IOException {
        // Given
        final SimpleBean bean = new SimpleBean(DefaultKasperId.random());

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(bean);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final SimpleBean actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), SimpleBean.class);

        // Then
        assertEquals(actualResponse.field, bean.field);
    }

    @Test
    public void test_ImmutableBean() throws IOException {
        // Given
        final ImmutableBean bean = new ImmutableBean(DefaultKasperRelationId.random());

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(bean);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final ImmutableBean actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), ImmutableBean.class);

        // Then
        assertEquals(actualResponse.field, bean.field);
    }

    @Test
    public void test_NoSettersBean() throws IOException {
        // Given
        final NoSettersBean bean = new NoSettersBean("test");

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(bean);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final NoSettersBean actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), NoSettersBean.class);

        // Then
        assertEquals(actualResponse.field, bean.field);
    }

}
