// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class HttpQueryExposerTest extends BaseHttpExposerTest<HttpQueryExposer> {

    public static class SomeCollectionQuery extends SomeQuery {
        private static final long serialVersionUID = 104409802777527460L;
    }

    public static class SomeCollectionResponse extends AbstractQueryCollectionResult<SomeResponse> {
    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class SomeCollectionQueryHandler implements QueryHandler<SomeCollectionQuery, SomeCollectionResponse> {
        @Override
        public QueryResponse<SomeCollectionResponse> retrieve(final QueryMessage<SomeCollectionQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();
            final SomeCollectionResponse list = new SomeCollectionResponse();
            final SomeResponse response = new SomeResponse();

            response.setQuery(q);
            list.setList(Arrays.asList(response));

            return QueryResponse.of(list);
        }
    }

    public static class UnknownQuery implements Query {
        private static final long serialVersionUID = 3548447022174239091L;
    }

    public static class SomeQuery implements Query {
        private static final long serialVersionUID = -7447288176593489294L;

        private String aValue;
        private int[] intArray;
        private boolean doThrowSomeException;

        private List<String> errorCodes;

        public int[] getIntArray() {
            return intArray;
        }

        public void setIntArray(final int[] intArray) {
            this.intArray = intArray;
        }

        public String getAValue() {
            return aValue;
        }

        public boolean isDoThrowSomeException() {
            return doThrowSomeException;
        }

        public void setAValue(final String aValue) {
            this.aValue = aValue;
        }

        public void setDoThrowSomeException(final boolean doThrowSomeException) {
            this.doThrowSomeException = doThrowSomeException;
        }

        public List<String> getErrorCodes() {
            return errorCodes;
        }

        public void setErrorCodes(final List<String> errorCodes) {
            this.errorCodes = errorCodes;
        }
    }

    public static class SomeResponse implements QueryResult {
        private SomeQuery query;

        public SomeQuery getQuery() {
            return query;
        }

        public void setQuery(final SomeQuery query) {
            this.query = query;
        }
    }

    @XKasperQueryHandler(domain = AccountDomain.class)
    public static class SomeQueryHandler implements QueryHandler<SomeQuery, SomeResponse> {
        @Override
        public QueryResponse<SomeResponse> retrieve(final QueryMessage<SomeQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();

            if (q.isDoThrowSomeException()) {
                final List<String> messages = new ArrayList<>();
                if (q.getErrorCodes() != null) {
                    for (int i = 0; i < q.getErrorCodes().size(); i++)
                        messages.add(q.getErrorCodes().get(i));
                }

                return QueryResponse.of(new KasperReason(q.aValue, messages));
            }

            final SomeResponse response = new SomeResponse();
            response.setQuery(q);
            return QueryResponse.of(response);
        }
    }

    // ------------------------------------------------------------------------
    private boolean usePostForQueries;

    public HttpQueryExposerTest(final boolean usePostForQueries) {
        this.usePostForQueries = usePostForQueries;
    }

    @Parameterized.Parameters public static Collection<Object[]> params() {
        Object[][] params = new Object[][] { {false}, {true}};
        return Lists.newArrayList(params);
    }

    @Override
    protected void customize(KasperClientBuilder clientBuilder) {
        clientBuilder.usePostForQueries(usePostForQueries);
    }

    // ------------------------------------------------------------------------

    @Override
    protected HttpQueryExposer createExposer(final ApplicationContext ctx) {
        return new HttpQueryExposer(ctx.getBean(QueryGateway.class), ctx.getBean(QueryHandlersLocator.class));
    }

    @Test
    public void testQueryRoundTrip() throws JsonProcessingException {
        // Given
        final SomeQuery query = new SomeQuery();
        query.aValue = "foo";
        query.doThrowSomeException = false;
        query.intArray = new int[] { 1, 2, 3 };

        // When
        final QueryResponse<SomeResponse> response = client().query(query, SomeResponse.class);

        // Then
        assertEquals(query.aValue, response.getResult().query.aValue);
        assertEquals(query.doThrowSomeException, response.getResult().query.doThrowSomeException);
        assertArrayEquals(query.intArray, response.getResult().query.intArray);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerThrowingException() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = true;
        query.aValue = "aaa";

        // When
        final QueryResponse<SomeResponse> actual = client().query(query, SomeResponse.class);

        // Then
        assertFalse(actual.isOK());
        assertEquals(query.aValue, actual.getReason().getCode());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, actual.asHttp().getHTTPStatus());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerReturningCollectionResponse() {
        // Given
        final SomeCollectionQuery query = new SomeCollectionQuery();

        // When
        final QueryResponse<SomeCollectionResponse> response = client().query(query, SomeCollectionResponse.class);

        // Then
        assertEquals(1, response.getResult().getCount());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryHandlerThrowListOfErrors() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.setDoThrowSomeException(true);
        query.setAValue("some error message");
        query.setErrorCodes(Arrays.asList("a", "b"));

        // When
        final QueryResponse<SomeCollectionResponse> actual = client().query(query, SomeCollectionResponse.class);
       
        // Then
        assertFalse(actual.isOK());
        assertEquals(query.getAValue(), actual.getReason().getCode());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, actual.asHttp().getHTTPStatus());
        final String[] actualMessages = actual.getReason().getMessages().toArray(new String[0]);
        for (int i = 0; i < query.getErrorCodes().size(); i++) {
            assertEquals(query.getErrorCodes().get(i), actualMessages[i]);
        }
    }

}
