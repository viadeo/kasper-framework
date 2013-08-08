package com.viadeo.kasper.exposition;

import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException.ExceptionBuilder;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionResult;
import com.viadeo.kasper.platform.Platform;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class HttpQueryExposerTest extends BaseHttpExposerTest<HttpQueryExposer> {

    public static class SomeCollectionQuery extends SomeQuery {
        private static final long serialVersionUID = 104409802777527460L;
    }

    public static class SomeCollectionResult extends AbstractQueryCollectionResult<SomeResult> {
        private static final long serialVersionUID = 8849846911146025322L;
    }

    @XKasperQueryService(domain = AccountDomain.class)
    public static class SomeCollectionQueryService implements QueryService<SomeCollectionQuery, SomeCollectionResult> {
        @Override
        public SomeCollectionResult retrieve(final QueryMessage<SomeCollectionQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();
            SomeCollectionResult list = new SomeCollectionResult();
            SomeResult result = new SomeResult();
            result.setQuery(q);
            list.setList(Arrays.asList(result));
            return list;
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

        public String getaValue() {
            return aValue;
        }

        public boolean isDoThrowSomeException() {
            return doThrowSomeException;
        }

        public void setaValue(final String aValue) {
            this.aValue = aValue;
        }

        public void setDoThrowSomeException(final boolean doThrowSomeException) {
            this.doThrowSomeException = doThrowSomeException;
        }

        public List<String> getErrorCodes() {
            return errorCodes;
        }

        public void setErrorCodes(List<String> errorCodes) {
            this.errorCodes = errorCodes;
        }
    }

    public static class SomeResult implements QueryResult {
        private static final long serialVersionUID = 4780302444624913577L;
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
        public SomeResult retrieve(final QueryMessage<SomeQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();

            if (q.isDoThrowSomeException()) {
                ExceptionBuilder builder = KasperQueryException.exception(q.aValue);
                if (q.getErrorCodes() != null) {
                    for (int i = 0; i < q.getErrorCodes().size(); i++)
                        builder.addError(q.getErrorCodes().get(i), "");
                }

                builder.throwEx();
            }

            SomeResult result = new SomeResult();
            result.setQuery(q);
            return result;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    protected HttpQueryExposer createExposer(ApplicationContext ctx) {
        return new HttpQueryExposer(ctx.getBean(Platform.class), ctx.getBean(QueryServicesLocator.class));
    }

    @Test
    public void testQueryRoundTrip() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.aValue = "foo";
        query.doThrowSomeException = false;
        query.intArray = new int[] { 1, 2, 3 };

        // When
        final SomeResult result = client().query(query, SomeResult.class);

        // Then
        assertEquals(query.aValue, result.query.aValue);
        assertEquals(query.doThrowSomeException, result.query.doThrowSomeException);
        assertArrayEquals(query.intArray, result.query.intArray);
    }

    // ------------------------------------------------------------------------

    @Test(expected = KasperQueryException.class)
    public void testQueryServiceThrowingException() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = true;
        query.aValue = "aaa";

        // When
        client().query(query, SomeResult.class);

        // Then raise exception
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryServiceReturningCollectionResult() {
        // Given
        final SomeCollectionQuery query = new SomeCollectionQuery();

        // When
        SomeCollectionResult result = client().query(query, SomeCollectionResult.class);

        // Then
        assertEquals(1, result.getCount());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryServiceThrowListOfErrors() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.setDoThrowSomeException(true);
        query.setaValue("some error message");
        query.setErrorCodes(Arrays.asList("a", "b"));

        // When
        try {
            client().query(query, SomeCollectionResult.class);
            fail();
        } catch (KasperQueryException e) {
            // Then
            assertEquals(query.getaValue(), e.getMessage());
            for (int i = 0; i < query.getErrorCodes().size(); i++) {
                assertEquals(query.getErrorCodes().get(i), e.getErrors().get().get(i).getCode());
            }
        }
    }

}
