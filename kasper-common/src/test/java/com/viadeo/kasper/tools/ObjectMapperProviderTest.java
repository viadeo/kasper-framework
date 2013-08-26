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
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionPayload;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ObjectMapperProviderTest {
    final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();

    static class SomePayload implements QueryPayload {
        private String str;

        public SomePayload() {
            
        }
        
        public SomePayload(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    static class SomeCollectionResult extends AbstractQueryCollectionPayload<SomePayload> {
    }

    // ------------------------------------------------------------------------

    @Test
    public void queryResultSuccessRoundTrip() throws IOException {
        // Given
        final QueryResult<SomePayload> expected = new QueryResult<SomePayload>(new SomePayload("foo"));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expected);

        final QueryResult<SomePayload> actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), new TypeReference<QueryResult<SomePayload>>() {});
        
        assertFalse(actual.isError());
        assertNull(actual.getError());
        assertEquals(expected.getPayload().getStr(), actual.getPayload().getStr());
    }

    @Test
    public void queryResultErrorRoundTrip() throws IOException {
        // Given
        final QueryResult<?> expected = QueryResult.of(new KasperError("CODE", "aCode", "aMessage"));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expected);
        @SuppressWarnings("unchecked")
        final QueryResult<?> actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), QueryResult.class);

        // Then
        assertTrue(actual.isError());
        assertEquals(expected.getError().getCode(), actual.getError().getCode());
        assertEquals(expected.getError().getMessages().size(), actual.getError().getMessages()
                .size());

        for (int i = 0; i < expected.getError().getMessages().size(); i++) {
            assertEquals(expected.getError().getMessages().get(i), actual.getError().getMessages()
                    .get(i));
        }
    }

    @Test
    public void deserializeErrorCommandResultWithSingleKasperError() throws IOException {
        // Given
        final KasperError expectedError = new KasperError(CoreErrorCode.UNKNOWN_ERROR, "some error");
        final CommandResult expectedResult = CommandResult.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResult);
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), CommandResult.class);

        // Then
        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedError.getCode(), actualResult.getError().getCode());
        assertEquals(expectedError.getMessages().get(0),
                actualResult.getError().getMessages().get(0));
    }

    @Test
    public void deserializeErrorCommandResultWithMultipleKasperError() throws IOException {
        // Given
        final KasperError expectedError = new KasperError(CoreErrorCode.CONFLICT, "too late...",
                "some error");

        final CommandResult expectedResult = CommandResult.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResult);
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), CommandResult.class);

        // Then
        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedError.getMessages().size(), actualResult.getError().getMessages()
                .size());

        assertEquals(expectedError.getCode(), actualResult.getError().getCode());

        for (int i = 0; i < expectedError.getMessages().size(); i++) {
            assertEquals(expectedError.getMessages().get(i), actualResult.getError().getMessages()
                    .get(i));
        }
    }

    @Test
    public void dontFailOnUnknownProperty() throws IOException {
        // Given
        final SomeCollectionResult result = new SomeCollectionResult();
        result.setList(Arrays.asList(new SomePayload("foo"), new SomePayload("bar")));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(result);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final SomeCollectionResult actual = objectReader.readValue(objectReader.getFactory()
                .createJsonParser(json), SomeCollectionResult.class);

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
