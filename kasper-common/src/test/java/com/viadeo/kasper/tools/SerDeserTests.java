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
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.MapQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

    private <T> T serDeserTest(final T object, Class<T> clazz) throws IOException {
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(object);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final T actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), clazz);
        return actualResponse;
    }

    private <T> String deserSerTest(final String json, Class<T> clazz) throws IOException {
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final T actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), clazz);
        final String new_json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(actualResponse);
        return new_json;
    }

    private <T> T deserTest(final String json, Class<T> clazz) throws IOException {
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final T actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), clazz);
        return actualResponse;
    }

    // -- test results --------------------------------------------------------

    public static class TestResult implements QueryResult {
        private String field;
        @JsonCreator TestResult(@JsonProperty("field") final String field) { this.field = field; }
        public String getField() { return this.field; }
        public boolean equals(final Object obj) {
            return (null != obj)
                    && obj.getClass().equals(this.getClass())
                    && ((TestResult) obj).field.equals(field);
        }
    }

    public static class TestCollectionResult extends CollectionQueryResult<TestResult> {}

    public static class TestMapResult extends MapQueryResult<TestResult> {}

    // ------------------------------------------------------------------------

    @Test
    public void test_SimpleBean() throws IOException {
        // Given
        final SimpleBean bean = new SimpleBean(DefaultKasperId.random());

        // When
        final SimpleBean actualResponse = serDeserTest(bean, SimpleBean.class);

        // Then
        assertEquals(actualResponse.field, bean.field);
    }

    @Test
    public void test_ImmutableBean() throws IOException {
        // Given
        final ImmutableBean bean = new ImmutableBean(DefaultKasperRelationId.random());

        // When
        final ImmutableBean actualResponse = serDeserTest(bean, ImmutableBean.class);

        // Then
        assertEquals(actualResponse.field, bean.field);
    }

    @Test
    public void test_NoSettersBean() throws IOException {
        // Given
        final NoSettersBean bean = new NoSettersBean("test");

        // When
        final NoSettersBean actualResponse = serDeserTest(bean, NoSettersBean.class);

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
        final TestCollectionResult actualResponse = serDeserTest(collect, TestCollectionResult.class);

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
        final TestCollectionResult actualResponse = serDeserTest(collect, TestCollectionResult.class);

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
        final TestCollectionResult actualResponse = serDeserTest(collect, TestCollectionResult.class);

        // Then
        assertEquals(actualResponse, collect);
    }

    @Test
    public void test_MapResultSingle() throws IOException {
        // Given
        final TestMapResult map = new TestMapResult().withMap(
            new HashMap<String, TestResult>() {{
                this.put("r1", new TestResult("42"));
            }}
        );

        // When
        final TestMapResult actualResponse = serDeserTest(map, TestMapResult.class);

        // Then
        assertEquals(actualResponse, map);
    }

    @Test
    public void test_MapResultMultiple() throws IOException {
        // Given
        final TestMapResult map = new TestMapResult().withMap(
            new HashMap<String, TestResult>() {{
                this.put("r1", new TestResult("42"));
                this.put("r2", new TestResult("43"));
            }}
        );

        // When
        final TestMapResult actualResponse = serDeserTest(map, TestMapResult.class);

        // Then
        assertEquals(actualResponse, map);
    }

    @Test
    public void test_MapResultEmpty() throws IOException {
        // Given
        final TestMapResult map = new TestMapResult().withMap(
                new HashMap<String, TestResult>()
        );

        // When
        final TestMapResult actualResponse = serDeserTest(map, TestMapResult.class);

        // Then
        assertEquals(actualResponse, map);
    }

    // ------------------------------------------------------------------------

    public static final String QUERY_RESPONSE_NORMAL = "{\"field\":\"test\"}";

    public static final String QUERY_RESPONSE_OLD =
            "{"
          +         "\"status\":\"ERROR\","
          +         "\"reason\":true,"
          +         "\"message\":\"[0000] - UNKNOWN_REASON\","
          +         "\"reasons\":[{"
          +             "\"id\":\"8701c6ae-242e-4e80-92e9-522dc1ba999b\","
          +             "\"code\":\"[0000] - UNKNOWN_REASON\","
          +             "\"message\":\"ERROR Submiting query[getMemberContactFacets] to Kasper platform.\""
          +         "},{"
          +             "\"id\":\"8701c6ae-242e-4e80-92e9-522dc1ba999b\","
          +             "\"code\":\"[0000] - UNKNOWN_REASON\","
          +             "\"message\":\"Failed to execute phase [query]\""
          +         "}]"
          + "}"
    ;

    public static final String UUID = "8701c6ae-242e-4e80-92e9-522dc1ba999b";
    public static final String MESG_1 = "ERROR Submiting query[getMemberContactFacets] to Kasper platform.";
    public static final String MESG_2 = "Failed to execute phase [query]";
    public static final String QUERY_RESPONSE_NEW =
            "{"
          +         "\"id\":\"" + UUID + "\","
          +         "\"status\":\"ERROR\","
          +         "\"code\":\"0000\","
          +         "\"label\":\"UNKNOWN_REASON\","
          +         "\"reason\":true,"
          +         "\"reasons\":[{"
          +             "\"message\":\"" + MESG_1 + "\""
          +         "},{"
          +             "\"message\":\"" + MESG_2 + "\""
          +         "}]"
          + "}"
    ;

    @Test
    public void test_query_deserialize_normal() throws IOException {
        // Given
        // When
        final String result_json = deserSerTest(QUERY_RESPONSE_NORMAL, NoSettersBean.class);

        // Then
        assertEquals(QUERY_RESPONSE_NORMAL, result_json);
    }

    @Test
    public void test_query_deserialize_old() throws IOException {
        // Given
        // When
        final String result_json = deserSerTest(QUERY_RESPONSE_OLD, QueryResponse.class);

        // Then
        assertEquals(QUERY_RESPONSE_OLD, result_json);
    }

    @Test
    public void test_query_deserialize_new() throws IOException {
        // Given
        // When
        final QueryResponse<?> response = deserTest(QUERY_RESPONSE_NEW, QueryResponse.class);

        // Then
        assertEquals(UUID, response.getReason().getId().toString());
        assertEquals(KasperResponse.Status.ERROR, response.getStatus());
        assertEquals(
                CoreReasonCode.UNKNOWN_REASON.toString(),
                response.getReason().getCode()
        );
        assertEquals(2, response.getReason().getMessages().size());
        assertEquals(MESG_1, response.getReason().getMessages().toArray()[0]);
        assertEquals(MESG_2, response.getReason().getMessages().toArray()[1]);
    }

}
