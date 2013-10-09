// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperError;
import com.viadeo.kasper.client.KasperClientBuilder;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionPayload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.ApplicationContext;

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

    public static class SomeCollectionResult extends AbstractQueryCollectionPayload<SomeResult> {
    }

    @XKasperQueryService(domain = AccountDomain.class)
    public static class SomeCollectionQueryService implements QueryService<SomeCollectionQuery, SomeCollectionResult> {
        @Override
        public QueryResult<SomeCollectionResult> retrieve(final QueryMessage<SomeCollectionQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();
            final SomeCollectionResult list = new SomeCollectionResult();
            final SomeResult result = new SomeResult();

            result.setQuery(q);
            list.setList(Arrays.asList(result));

            return QueryResult.of(list);
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

    public static class SomeResult implements QueryPayload {
        private SomeQuery query;

        public SomeQuery getQuery() {
            return query;
        }

        public void setQuery(final SomeQuery query) {
            this.query = query;
        }
    }

    @XKasperQueryService(domain = AccountDomain.class)
    public static class SomeQueryService implements QueryService<SomeQuery, SomeResult> {
        @Override
        public QueryResult<SomeResult> retrieve(final QueryMessage<SomeQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();

            if (q.isDoThrowSomeException()) {
                List<String> messages = new ArrayList<>();
                if (q.getErrorCodes() != null) {
                    for (int i = 0; i < q.getErrorCodes().size(); i++)
                        messages.add(q.getErrorCodes().get(i));
                }

                return QueryResult.of(new KasperError(q.aValue, messages));
            }

            SomeResult result = new SomeResult();
            result.setQuery(q);
            return QueryResult.of(result);
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
        return new HttpQueryExposer(ctx.getBean(QueryGateway.class), ctx.getBean(QueryServicesLocator.class));
    }

    @Test
    public void testQueryRoundTrip() throws JsonProcessingException {
        // Given
        final SomeQuery query = new SomeQuery();
        query.aValue = "foo";
        query.doThrowSomeException = false;
        query.intArray = new int[] { 1, 2, 3 };

        // When
        final QueryResult<SomeResult> result = client().query(query, SomeResult.class);

        // Then
        assertEquals(query.aValue, result.getPayload().query.aValue);
        assertEquals(query.doThrowSomeException, result.getPayload().query.doThrowSomeException);
        assertArrayEquals(query.intArray, result.getPayload().query.intArray);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryServiceThrowingException() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = true;
        query.aValue = "aaa";

        // When
        QueryResult<SomeResult> actual = client().query(query, SomeResult.class);

        // Then
        assertTrue(actual.isError());
        assertEquals(query.aValue, actual.getError().getCode());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryServiceReturningCollectionResult() {
        // Given
        final SomeCollectionQuery query = new SomeCollectionQuery();

        // When
        final QueryResult<SomeCollectionResult> result = client().query(query, SomeCollectionResult.class);

        // Then
        assertEquals(1, result.getPayload().getCount());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryServiceThrowListOfErrors() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.setDoThrowSomeException(true);
        query.setAValue("some error message");
        query.setErrorCodes(Arrays.asList("a", "b"));

        // When
        final QueryResult<SomeCollectionResult> actual = client().query(query, SomeCollectionResult.class);
       
        // Then
        assertEquals(query.getAValue(), actual.getError().getCode());
        for (int i = 0; i < query.getErrorCodes().size(); i++) {
            assertEquals(query.getErrorCodes().get(i), actual.getError().getMessages().get(i));
        }
    }

}
