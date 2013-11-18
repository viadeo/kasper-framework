// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ObjectMapperProviderTest {
    final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();

    static class SomeResult implements QueryResult {
        private String str;

        public SomeResult() {
            
        }
        
        public SomeResult(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    static class SomeCollectionResponse extends CollectionQueryResult<SomeResult> {
    }

    // ------------------------------------------------------------------------

    @Test
    public void queryResponseSuccessRoundTrip() throws IOException {
        // Given
        final QueryResponse<SomeResult> expected = new QueryResponse<SomeResult>(new SomeResult("foo"));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expected);

        final QueryResponse<SomeResult> actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), new TypeReference<QueryResponse<SomeResult>>() {});
        
        assertTrue(actual.isOK());
        assertNull(actual.getReason());
        assertEquals(expected.getResult().getStr(), actual.getResult().getStr());
    }

    @Test
    public void queryResponseErrorRoundTrip() throws IOException {
        // Given
        final QueryResponse expected = QueryResponse.error(new KasperReason("CODE", "aCode", "aMessage"));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expected);
        @SuppressWarnings("unchecked")
        final QueryResponse actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), QueryResponse.class);

        // Then
        assertFalse(actual.isOK());
        assertEquals(expected.getReason().getCode(), actual.getReason().getCode());
        assertEquals(expected.getReason().getMessages().size(),
                     actual.getReason().getMessages().size());

        for (int i = 0; i < expected.getReason().getMessages().size(); i++) {
            assertEquals(expected.getReason().getMessages().toArray()[i],
                         actual.getReason().getMessages().toArray()[i]);
        }
    }

    @Test
    public void deserializeErrorCommandResponseWithSingleKasperReason() throws IOException {
        // Given
        final KasperReason expectedError = new KasperReason(CoreReasonCode.UNKNOWN_REASON, "some error");
        final CommandResponse expectedResponse = CommandResponse.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResponse);
        final CommandResponse actualResponse = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), CommandResponse.class);

        // Then
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedError.getCode(), actualResponse.getReason().getCode());
        assertEquals(expectedError.getMessages().toArray()[0],
                     actualResponse.getReason().getMessages().toArray()[0]);
    }

    @Test
    public void deserializeErrorCommandResponseWithMultipleKasperReason() throws IOException {
        // Given
        final KasperReason expectedError = new KasperReason(CoreReasonCode.CONFLICT, "too late...",
                "some error");

        final CommandResponse expectedResponse = CommandResponse.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResponse);
        final CommandResponse actualResponse = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), CommandResponse.class);

        // Then
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedError.getMessages().size(), actualResponse.getReason().getMessages()
                .size());

        assertEquals(expectedError.getCode(), actualResponse.getReason().getCode());

        for (int i = 0; i < expectedError.getMessages().size(); i++) {
            assertEquals(expectedError.getMessages().toArray()[i],
                         actualResponse.getReason().getMessages().toArray()[i]);
        }
    }

    @Test
    public void dontFailOnUnknownProperty() throws IOException {
        // Given
        final SomeCollectionResponse response = new SomeCollectionResponse();
        response.setList(Arrays.asList(new SomeResult("foo"), new SomeResult("bar")));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(response);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final SomeCollectionResponse actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), SomeCollectionResponse.class);

        // Then
        assertEquals(response.getCount(), actual.getCount());
    }

    @Test
    public void serializeDateTimeToISO8601() throws IOException {
        // Given
        final DateTime dateTime = new DateTime(2013, 8, 6, 7, 35, 0, 123, DateTimeZone.UTC);

        // When
        final String actual = ObjectMapperProvider.INSTANCE.mapper().writeValueAsString(dateTime);

        // Then
        assertEquals("\"2013-08-06T07:35:00.123Z\"", actual);
    }

    @Test
    public void deserializeISO8601DateToDateTime() throws IOException {
        // Given
        final String jsonIso8601 = "\"2013-08-06T07:35:00.123Z\"";

        // When
        final DateTime actual = ObjectMapperProvider.INSTANCE.mapper().readValue(jsonIso8601,
                DateTime.class);

        // Then
        final DateTime expectedDateTime = new DateTime(2013, 8, 6, 7, 35, 0, 123, DateTimeZone.UTC);
        assertEquals(expectedDateTime, actual);
    }

}
