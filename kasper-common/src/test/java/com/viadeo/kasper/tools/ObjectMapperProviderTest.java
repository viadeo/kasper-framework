// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class ObjectMapperProviderTest {

    final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();

    static class SomeResult implements QueryResult {
        private static final long serialVersionUID = 7036268990439270899L;

        private String str;

        public SomeResult() { }

        public SomeResult(final String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public void setStr(final String str) {
            this.str = str;
        }
    }

    static class SomeCollectionResponse extends CollectionQueryResult<SomeResult> {
        private static final long serialVersionUID = 7698126469953546332L;

        SomeCollectionResponse(Collection<SomeResult> list) {
            super(list);
        }
    }

    public static class ImmutableQuery implements Query {
        private static final long serialVersionUID = 2139044505564060435L;

        private final String name;
        private final Integer value;

        public ImmutableQuery(final String name, final Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void queryResponseSuccessRoundTrip() throws IOException {
        // Given
        final QueryResponse<SomeResult> expected = new QueryResponse<SomeResult>(new SomeResult("foo"));

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
            expected
        );

        final QueryResponse<SomeResult> actual = objectReader.readValue(
            objectReader.getFactory().createParser(json),
            new TypeReference<QueryResponse<SomeResult>>() {}
        );
        
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
                expected
        );

        @SuppressWarnings("unchecked")
        final QueryResponse actual = objectReader.readValue(
            objectReader.getFactory().createParser(json),
            QueryResponse.class
        );

        // Then
        assertFalse(actual.isOK());
        assertEquals(expected.getReason().getReasonCode(), actual.getReason().getReasonCode());
        assertEquals(
            expected.getReason().getMessages().size(),
            actual.getReason().getMessages().size()
        );

        for (int i = 0; i < expected.getReason().getMessages().size(); i++) {
            assertEquals(
                expected.getReason().getMessages().toArray()[i],
                actual.getReason().getMessages().toArray()[i]
            );
        }
    }

    @Test
    public void deserializeErrorCommandResponseWithSingleKasperReason() throws IOException {
        // Given
        final KasperReason expectedError = new KasperReason(CoreReasonCode.UNKNOWN_REASON, "some error");
        final CommandResponse expectedResponse = CommandResponse.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
            expectedResponse
        );
        final CommandResponse actualResponse = objectReader.readValue(
            objectReader.getFactory().createParser(json),
            CommandResponse.class
        );

        // Then
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedError.getCode(), actualResponse.getReason().getCode());
        assertEquals(
                expectedError.getMessages().toArray()[0],
                actualResponse.getReason().getMessages().toArray()[0]
        );
    }

    @Test
    public void deserializeErrorCommandResponseWithMultipleKasperReason() throws IOException {
        // Given
        final KasperReason expectedError = new KasperReason(CoreReasonCode.CONFLICT, "too late...",
                "some error");

        final CommandResponse expectedResponse = CommandResponse.error(expectedError);

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(
                expectedResponse
        );
        final CommandResponse actualResponse = objectReader.readValue(
                objectReader.getFactory().createParser(json),
                CommandResponse.class
        );

        // Then
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(
                expectedError.getMessages().size(),
                actualResponse.getReason().getMessages().size()
        );

        assertEquals(expectedError.getCode(), actualResponse.getReason().getCode());

        for (int i = 0; i < expectedError.getMessages().size(); i++) {
            assertEquals(
                    expectedError.getMessages().toArray()[i],
                    actualResponse.getReason().getMessages().toArray()[i]
            );
        }
    }

    @Test
    public void dontFailOnUnknownProperty() throws IOException {
        // Given
        final SomeCollectionResponse response = new SomeCollectionResponse(
                Arrays.asList(new SomeResult("foo"), new SomeResult("bar"))
        );

        // When
        final String json = ObjectMapperProvider.INSTANCE.objectWriter().writeValueAsString(response);
        final ObjectReader objectReader = ObjectMapperProvider.INSTANCE.objectReader();
        final SomeCollectionResponse actual = objectReader.readValue(
                objectReader.getFactory().createParser(json),
                SomeCollectionResponse.class
        );

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
        final DateTime actual = ObjectMapperProvider.INSTANCE.mapper().readValue(
                jsonIso8601,
                DateTime.class
        );

        // Then
        final DateTime expectedDateTime = new DateTime(2013, 8, 6, 7, 35, 0, 123, DateTimeZone.UTC);
        assertEquals(expectedDateTime, actual);
    }

    @Test
    public void serDeserDateTime() throws IOException {
        // Given
        final ObjectMapper mapper = ObjectMapperProvider.INSTANCE.mapper();
        final DateTime expectedDate = DateTime.now();

        // When
        final String json = mapper.writeValueAsString(expectedDate);
        final DateTime actualDate = mapper.reader(DateTime.class).readValue(json);

        // Then
        assertTrue(expectedDate.isEqual(actualDate));
    }

    @Test
    public void serializeMoneyToISO4217() throws IOException {
        // Given
        final Money money = Money.of(CurrencyUnit.EUR, new BigDecimal("19.99"));

        // When
        final String actual = ObjectMapperProvider.INSTANCE.mapper().writeValueAsString(money);

        // Then
        assertEquals("\"EUR 19.99\"", actual);
    }

    @Test
    public void deserializeISO4217ToMoney() throws IOException {
        // Given
        final String moneyIso4217 = "\"EUR 19.99\"";

        // When
        final Money actual = ObjectMapperProvider.INSTANCE.mapper().readValue(moneyIso4217, Money.class);

        // Then
        final Money expected = Money.of(CurrencyUnit.EUR, new BigDecimal("19.99"));
        assertEquals(expected, actual);
    }

    @Test(expected = JsonMappingException.class)
    public void deserializeNumberToMoney_fails() throws IOException {
        // Given
        final String number = "19";

        // When
        final Money actual = ObjectMapperProvider.INSTANCE.mapper().readValue(number, Money.class);
    }

    @Test
    public void serializeImmutableClass() throws Exception {
        // Given
        final ImmutableQuery immutableObject = new ImmutableQuery("foobar", 42);

        // When
        String json = ObjectMapperProvider.INSTANCE.mapper().writeValueAsString(immutableObject);

        // Then
        assertEquals("{\"name\":\"foobar\",\"value\":42}", json);

    }

    @Test
    public void deserializeImmutableClass() throws Exception {
        // Given
        final ObjectMapper mapper = ObjectMapperProvider.INSTANCE.mapper();

        // When
        ImmutableQuery actual = mapper.readValue("{\"name\":\"foobar\",\"value\":42}", ImmutableQuery.class);

        // Then
        assertNotNull(actual);
        assertEquals("foobar", actual.getName());
        assertEquals((Integer)42, actual.getValue());
    }

}
