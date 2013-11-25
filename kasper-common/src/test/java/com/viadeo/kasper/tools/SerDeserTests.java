// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.MapQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    // -- test results --------------------------------------------------------

    public static class TestResult implements QueryResult {
        private String field;
        @JsonCreator
        TestResult(@JsonProperty("field") final String field) { this.field = field; }
        public String getField() { return this.field; }
        public boolean equals(final Object obj) {
            return (null != obj)
                    && obj.getClass().equals(this.getClass())
                    && ((TestResult) obj).field.equals(field);
        }
    }

    public static class TestMapResult extends MapQueryResult<TestResult> {}

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

    @Test
    public void test_MapResultSingle() throws IOException {
        // Given
        Map<String,TestResult> mapResult = new HashMap<String,TestResult>();
        mapResult.put("r1",new TestResult("42"));

        final TestMapResult collect = new TestMapResult().withMap(mapResult);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(collect);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final TestMapResult actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), TestMapResult.class);

        // Then
        assertEquals(actualResponse, collect);

    }

    @Test
    public void test_MapResultMultiple() throws IOException {
        // Given
        Map<String,TestResult> mapResult = new HashMap<String,TestResult>();
        mapResult.put("r1",new TestResult("42"));
        mapResult.put("r2",new TestResult("43"));

        final TestMapResult collect = new TestMapResult().withMap(mapResult);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(collect);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final TestMapResult actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), TestMapResult.class);

        // Then
        assertEquals(actualResponse, collect);

    }
    @Test
    public void test_MapResultEmpty() throws IOException {
        // Given
        Map<String,TestResult> mapResult = new HashMap<String,TestResult>();

        final TestMapResult collect = new TestMapResult().withMap(mapResult);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(collect);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final TestMapResult actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), TestMapResult.class);

        // Then
        assertEquals(actualResponse, collect);

    }
}
