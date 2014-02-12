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
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.MapQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class SerDeserTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
                { true },
                { false }
        };
        return Arrays.asList(data);
    }

    // ------------------------------------------------------------------------

    private final Boolean useNewSerialization;
    private final ObjectMapperProvider omProvider;

    public SerDeserTests(final Boolean useNewSerialization) {
        omProvider = new ObjectMapperProvider();

        if ( useNewSerialization) {
           omProvider.mapper().registerModule(
               new SimpleModule()
                   .addSerializer(CommandResponse.class, new CommandResponseNewSerializer())
                   .addSerializer(QueryResponse.class, new QueryResponseNewSerializer())
           );
        }

        this.useNewSerialization = useNewSerialization;
    }

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
        final String json = omProvider.objectWriter().writeValueAsString(object);
        final ObjectReader objectReader = omProvider.objectReader();
        final T actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), clazz);
        return actualResponse;
    }

    private <T> String deserSerTest(final String json, Class<T> clazz) throws IOException {
        final ObjectReader objectReader = omProvider.objectReader();
        final T actualResponse = objectReader.readValue(objectReader.getFactory().createJsonParser(json), clazz);
        final String new_json = omProvider.objectWriter().writeValueAsString(actualResponse);
        return new_json;
    }

    private <T> T deserTest(final String json, Class<T> clazz) throws IOException {
        final ObjectReader objectReader = omProvider.objectReader();
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

    public static final String QUERY_UUID = "8701c6ae-242e-4e80-92e9-522dc1ba999b";
    public static final String QUERY_MESG_1 = "ERROR Submiting query[getMemberContactFacets] to Kasper platform.";
    public static final String QUERY_MESG_2 = "Failed to execute phase [query]";
    public static final String QUERY_RESPONSE_NEW =
            "{"
          +         "\"id\":\"" + QUERY_UUID + "\","
          +         "\"status\":\"ERROR\","
          +         "\"code\":\"0000\","
          +         "\"label\":\"UNKNOWN_REASON\","
          +         "\"reason\":true,"
          +         "\"reasons\":[{"
          +             "\"message\":\"" + QUERY_MESG_1 + "\""
          +         "},{"
          +             "\"message\":\"" + QUERY_MESG_2 + "\""
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
        if ( ! useNewSerialization) {
            // Given
            // When
            final String result_json = deserSerTest(QUERY_RESPONSE_OLD, QueryResponse.class);

            // Then
            assertEquals(QUERY_RESPONSE_OLD, result_json);
        }
    }

    @Test
    public void test_query_deserialize_new() throws IOException {
        // Given
        // When
        final QueryResponse<?> response = deserTest(QUERY_RESPONSE_NEW, QueryResponse.class);

        // Then
        assertEquals(QUERY_UUID, response.getReason().getId().toString());
        assertEquals(KasperResponse.Status.ERROR, response.getStatus());
        assertEquals(
                CoreReasonCode.UNKNOWN_REASON.toString(),
                response.getReason().getCode()
        );
        assertEquals(2, response.getReason().getMessages().size());
        assertEquals(QUERY_MESG_1, response.getReason().getMessages().toArray()[0]);
        assertEquals(QUERY_MESG_2, response.getReason().getMessages().toArray()[1]);
    }

    // ------------------------------------------------------------------------

    public static final String COMMAND_RESPONSE_NORMAL = "{\"status\":\"OK\",\"reason\":false,\"reasons\":[]}";

    public static final String COMMAND_RESPONSE_OLD =
            "{"
                    +         "\"status\":\"ERROR\","
                    +         "\"reason\":true,"
                    +         "\"message\":\"[0000] - UNKNOWN_REASON\","
                    +         "\"reasons\":[{"
                    +             "\"id\":\"8701c6ae-242e-4e80-92e9-522dc1ba999b\","
                    +             "\"code\":\"[0000] - UNKNOWN_REASON\","
                    +             "\"message\":\"ERROR Submiting command to Kasper platform.\""
                    +         "},{"
                    +             "\"id\":\"8701c6ae-242e-4e80-92e9-522dc1ba999b\","
                    +             "\"code\":\"[0000] - UNKNOWN_REASON\","
                    +             "\"message\":\"Failed to execute phase [command]\""
                    +         "}]"
                    + "}"
            ;

    public static final String COMMAND_UUID = "6de93ba2-46ee-4f68-820e-a745a30c7599";
    public static final String COMMAND_MESG_1 = "ERROR Submiting command to Kasper platform.";
    public static final String COMMAND_MESG_2 = "Failed to execute phase [command]";
    public static final String COMMAND_RESPONSE_NEW =
            "{"
                    +         "\"id\":\"" + COMMAND_UUID + "\","
                    +         "\"status\":\"ERROR\","
                    +         "\"code\":\"0000\","
                    +         "\"label\":\"UNKNOWN_REASON\","
                    +         "\"reason\":true,"
                    +         "\"reasons\":[{"
                    +             "\"message\":\"" + COMMAND_MESG_1 + "\""
                    +         "},{"
                    +             "\"message\":\"" + COMMAND_MESG_2 + "\""
                    +         "}]"
                    + "}"
            ;

    @Test
    public void test_command_deserialize_normal() throws IOException {
        // Given
        final String json = omProvider.objectWriter().writeValueAsString(
                CommandResponse.ok()
        );

        // Then
        assertEquals(COMMAND_RESPONSE_NORMAL, json);

        // When
        final String result_json = deserSerTest(COMMAND_RESPONSE_NORMAL, CommandResponse.class);

        // Then
        assertEquals(COMMAND_RESPONSE_NORMAL, result_json);
    }

    @Test
    public void test_command_deserialize_old() throws IOException {
        if ( ! useNewSerialization) {
            // Given
            // When
            final String result_json = deserSerTest(COMMAND_RESPONSE_OLD, CommandResponse.class);

            // Then
            assertEquals(COMMAND_RESPONSE_OLD, result_json);
        }
    }

    @Test
    public void test_command_deserialize_new() throws IOException {
        // Given
        // When
        final CommandResponse response = deserTest(COMMAND_RESPONSE_NEW, CommandResponse.class);

        // Then
        assertEquals(COMMAND_UUID, response.getReason().getId().toString());
        assertEquals(KasperResponse.Status.ERROR, response.getStatus());
        assertEquals(
                CoreReasonCode.UNKNOWN_REASON.toString(),
                response.getReason().getCode()
        );
        assertEquals(2, response.getReason().getMessages().size());
        assertEquals(COMMAND_MESG_1, response.getReason().getMessages().toArray()[0]);
        assertEquals(COMMAND_MESG_2, response.getReason().getMessages().toArray()[1]);
    }

}
