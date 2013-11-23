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
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

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

    public static class TestCollectionResult extends CollectionQueryResult<TestResult> {}

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

    // ------------------------------------------------------------------------

    @Test
    public void test_CollectionResultSingle() throws IOException {
        // Given
        final TestResult result = new TestResult("42");
        final TestCollectionResult collect = new TestCollectionResult().withList(
            new ArrayList<TestResult>() {{
                this.add(result);
            }}
        );

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(collect);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final TestCollectionResult actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), TestCollectionResult.class);

        // Then
        assertEquals(actualResponse, collect);

    }

    @Test
    public void test_CollectionResultMultiple() throws IOException {
        // Given
        final TestResult result = new TestResult("42");
        final TestResult result2 = new TestResult("24");
        final TestCollectionResult collect = new TestCollectionResult().withList(
                new ArrayList<TestResult>() {{
                    this.add(result);
                    this.add(result2);
                }}
        );

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(collect);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final TestCollectionResult actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), TestCollectionResult.class);

        // Then
        assertEquals(actualResponse, collect);

    }

    @Test
    public void test_CollectionResultEmpty_1() throws IOException {
        // Given
        final TestCollectionResult collect = new TestCollectionResult().withList(
                new ArrayList<TestResult>()
        );

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(collect);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final TestCollectionResult actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), TestCollectionResult.class);

        // Then
        assertEquals(actualResponse, collect);

    }

}
