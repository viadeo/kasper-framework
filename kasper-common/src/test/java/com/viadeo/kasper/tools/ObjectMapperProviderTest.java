// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.cqrs.command.CommandResult;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionResult;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObjectMapperProviderTest {

    static class SomeResult implements QueryResult {
        private static final long serialVersionUID = -3621614243017076348L;

        public String getStr() {
            return "str";
        }
    }

    static class SomeCollectionResult extends AbstractQueryCollectionResult<SomeResult> {
        private static final long serialVersionUID = 8849846914246025322L;
    }

    // ------------------------------------------------------------------------
    
    @Test
    public void queryExceptionRoundTrip() throws IOException {
        // Given
        final KasperQueryException expected = new KasperQueryException("some message", null,
                Arrays.asList(new KasperError("aCode", "aMessage")));
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();

        //When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expected);
        final KasperQueryException actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                KasperQueryException.class);

        // Then
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getErrors().get().size(), actual.getErrors().get().size());
        
        for (int i = 0; i < expected.getErrors().get().size(); i++) {
            assertEquals(expected.getErrors().get().get(i), actual.getErrors().get().get(i));
        }
    }

    @Test
    public void deserializeSingleKasperError() throws IOException {
        // Given
        final KasperError expected = new KasperError(KasperError.UNKNOWN_ERROR, "some error");

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expected);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final KasperError actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                KasperError.class);

        // Then
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    public void deserializeErrorCommandResultWithSingleKasperError() throws IOException {
        // Given
        final KasperError expectedError = new KasperError(KasperError.UNKNOWN_ERROR, "some error");
        final CommandResult expectedResult = CommandResult.error().addError(expectedError).create();

        //When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expectedResult);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                CommandResult.class);

        // Then
        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedResult.getErrors().get().get(0).getCode(), actualResult.getErrors().get().get(0).getCode());
        assertEquals(expectedResult.getErrors().get().get(0).getMessage(), actualResult.getErrors().get().get(0).getMessage());
    }

    @Test
    public void deserializeErrorCommandResultWithMultipleKasperError() throws IOException {
        // Given
        final List<KasperError> expectedErrors = Arrays.asList(new KasperError(KasperError.CONFLICT, "too late..."),
                new KasperError(KasperError.UNKNOWN_ERROR, "some error"));

        final CommandResult expectedResult = CommandResult.error().addErrors(expectedErrors).create();

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expectedResult);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                CommandResult.class);

        // Then
        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedErrors.size(), actualResult.getErrors().get().size());

        for (int i = 0; i < expectedErrors.size(); i++) {
            assertEquals(expectedResult.getErrors().get().get(i).getCode(), actualResult.getErrors().get().get(i).getCode());
            assertEquals(expectedResult.getErrors().get().get(i).getMessage(), actualResult.getErrors().get().get(i).getMessage());
        }
    }

    @Test
    public void deserializeErrorCommandResultWithNoKasperError() throws IOException {
        // Given
        final CommandResult expectedResult = CommandResult.error().create();

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(expectedResult);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final CommandResult actualResult = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                CommandResult.class);

        // Then
        assertEquals(expectedResult.getStatus(), actualResult.getStatus());
        assertEquals(expectedResult.getErrors().get().size(), actualResult.getErrors().get().size());
    }

    @Test
    public void dontFailOnUnknownProperty() throws IOException {
        // Given
        final SomeCollectionResult result = new SomeCollectionResult();
        result.setList(Arrays.asList(new SomeResult(), new SomeResult()));

        // When
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(result);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final SomeCollectionResult actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json),
                SomeCollectionResult.class);

        // Then
        assertEquals(result.getCount(), actual.getCount());
    }

    @Test
    public void serializeDateTimeToISO8601() throws IOException {
        // Given
        final DateTime dateTime = new DateTime(2013, 8, 6, 7, 35, 0, 123, DateTimeZone.UTC);

        // When
        final String actual = ObjectMapperProvider.instance.mapper().writeValueAsString(dateTime);

        // Then
        assertEquals("\"2013-08-06T07:35:00.123Z\"", actual);
    }

    @Test
    public void deserializeISO8601DateToDateTime() throws IOException {
        // Given
        final String jsonIso8601 = "\"2013-08-06T07:35:00.123Z\"";

        // When
        final DateTime actual = ObjectMapperProvider.instance.mapper().readValue(jsonIso8601, DateTime.class);

        // Then
        final DateTime expectedDateTime = new DateTime(2013, 8, 6, 7, 35, 0, 123, DateTimeZone.UTC);
        assertEquals(expectedDateTime, actual);
    }

}
