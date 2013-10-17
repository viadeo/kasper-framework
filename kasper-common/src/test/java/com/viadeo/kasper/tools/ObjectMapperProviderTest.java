// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.CoreErrorCode;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionResult;
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

    static class SomeCollectionResponse extends AbstractQueryCollectionResult<SomeResult> {
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
        
        assertFalse(actual.isError());
        assertNull(actual.getError());
        assertEquals(expected.getResult().getStr(), actual.getResult().getStr());
    }

    @Test
    public void queryResponseErrorRoundTrip() throws IOException {
        // Given
        final QueryResponse expected = QueryResponse.of(new KasperError("CODE", "aCode", "aMessage"));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expected);
        @SuppressWarnings("unchecked")
        final QueryResponse actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), QueryResponse.class);

        // Then
        assertTrue(actual.isError());
        assertEquals(expected.getError().getCode(), actual.getError().getCode());
        assertEquals(expected.getError().getMessages().size(),
                     actual.getError().getMessages().size());

        for (int i = 0; i < expected.getError().getMessages().size(); i++) {
            assertEquals(expected.getError().getMessages().toArray()[i],
                         actual.getError().getMessages().toArray()[i]);
        }
    }

    @Test
    public void deserializeErrorCommandResponseWithSingleKasperError() throws IOException {
        // Given
        final KasperError expectedError = new KasperError(CoreErrorCode.UNKNOWN_ERROR, "some error");
        final CommandResponse expectedResponse = CommandResponse.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResponse);
        final CommandResponse actualResponse = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), CommandResponse.class);

        // Then
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedError.getCode(), actualResponse.getError().getCode());
        assertEquals(expectedError.getMessages().toArray()[0],
                     actualResponse.getError().getMessages().toArray()[0]);
    }

    @Test
    public void deserializeErrorCommandResponseWithMultipleKasperError() throws IOException {
        // Given
        final KasperError expectedError = new KasperError(CoreErrorCode.CONFLICT, "too late...",
                "some error");

        final CommandResponse expectedResponse = CommandResponse.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResponse);
        final CommandResponse actualResponse = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), CommandResponse.class);

        // Then
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedError.getMessages().size(), actualResponse.getError().getMessages()
                .size());

        assertEquals(expectedError.getCode(), actualResponse.getError().getCode());

        for (int i = 0; i < expectedError.getMessages().size(); i++) {
            assertEquals(expectedError.getMessages().toArray()[i],
                         actualResponse.getError().getMessages().toArray()[i]);
        }
    }

    @Test
    public void dontFailOnUnknownProperty() throws IOException {
        // Given
        final SomeCollectionResponse result = new SomeCollectionResponse();
        result.setList(Arrays.asList(new SomeResult("foo"), new SomeResult("bar")));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(result);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final SomeCollectionResponse actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), SomeCollectionResponse.class);

        // Then
        assertEquals(result.getCount(), actual.getCount());
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
