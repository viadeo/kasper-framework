package com.viadeo.kasper.exposition;

import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException.ExceptionBuilder;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionDTO;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
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

    public static class SomeCollectionDTO extends AbstractQueryCollectionDTO<SomeDto> {
        private static final long serialVersionUID = 8849846911146025322L;
    }

    @XKasperQueryService(domain = AccountDomain.class)
    public static class SomeCollectionQueryService implements QueryService<SomeCollectionQuery, SomeCollectionDTO> {
        @Override
        public SomeCollectionDTO retrieve(final QueryMessage<SomeCollectionQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();
            SomeCollectionDTO list = new SomeCollectionDTO();
            SomeDto dto = new SomeDto();
            dto.setQuery(q);
            list.setList(Arrays.asList(dto));
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

    public static class SomeDto implements QueryDTO {
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
    public static class SomeQueryService implements QueryService<SomeQuery, SomeDto> {
        @Override
        public SomeDto retrieve(final QueryMessage<SomeQuery> message) throws KasperQueryException {
            final SomeQuery q = message.getQuery();

            if (q.isDoThrowSomeException()) {
                ExceptionBuilder builder = KasperQueryException.exception(q.aValue);
                if (q.getErrorCodes() != null) {
                    for (int i = 0; i < q.getErrorCodes().size(); i++)
                        builder.addError(q.getErrorCodes().get(i), "");
                }

                builder.throwEx();
            }

            SomeDto dto = new SomeDto();
            dto.setQuery(q);
            return dto;
        }
    }

    public static class AccountDomain extends AbstractDomain {
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
        final SomeDto dto = client().query(query, SomeDto.class);

        // Then
        assertEquals(query.aValue, dto.query.aValue);
        assertEquals(query.doThrowSomeException, dto.query.doThrowSomeException);
        assertArrayEquals(query.intArray, dto.query.intArray);
    }

    // ------------------------------------------------------------------------

    @Test(expected = KasperQueryException.class)
    public void testQueryServiceThrowingException() {
        // Given
        final SomeQuery query = new SomeQuery();
        query.doThrowSomeException = true;
        query.aValue = "aaa";

        // When
        client().query(query, SomeDto.class);

        // Then raise exception
    }

    // ------------------------------------------------------------------------

    @Test
    public void testQueryServiceReturningCollectionDTO() {
        // Given
        final SomeCollectionQuery query = new SomeCollectionQuery();

        // When
        SomeCollectionDTO dto = client().query(query, SomeCollectionDTO.class);

        // Then
        assertEquals(1, dto.getCount());
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
            client().query(query, SomeCollectionDTO.class);
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
