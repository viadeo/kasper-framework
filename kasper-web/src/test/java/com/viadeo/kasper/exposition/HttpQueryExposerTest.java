package com.viadeo.kasper.exposition;

import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionDTO;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import com.viadeo.kasper.platform.IPlatform;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HttpQueryExposerTest extends BaseHttpExposerTest<HttpQueryExposer> {

    public static class SomeCollectionQuery extends SomeQuery {
        private static final long serialVersionUID = 104409802777527460L;
	}

    public static class SomeCollectionDTO extends AbstractQueryCollectionDTO<SomeDto> {
        private static final long serialVersionUID = 8849846911146025322L;
    }

	@XKasperQueryService(domain=AccountDomain.class)
    public static class SomeCollectionQueryService implements IQueryService<SomeCollectionQuery, SomeCollectionDTO> {
        @Override
        public SomeCollectionDTO retrieve(final IQueryMessage<SomeCollectionQuery> message)
                throws KasperQueryException {
            final SomeQuery q = message.getQuery();
            SomeCollectionDTO list = new SomeCollectionDTO();
            SomeDto dto = new SomeDto();
            dto.setQuery(q);
            list.setList(Arrays.asList(dto));
            return list;
        }
    }

	public static class UnknownQuery implements IQuery {
		private static final long serialVersionUID = 3548447022174239091L;
	}

	public static class SomeQuery implements IQuery {
		private static final long serialVersionUID = -7447288176593489294L;

		private String aValue;
		private int[] intArray;
		private boolean doThrowSomeException;

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
	}

	public static class SomeDto implements IQueryDTO {
		private static final long serialVersionUID = 4780302444624913577L;
		private SomeQuery query;

		public SomeQuery getQuery() {
			return query;
		}

		public void setQuery(final SomeQuery query) {
			this.query = query;
		}
	}

	@XKasperQueryService(domain=AccountDomain.class)
	public static class SomeQueryService implements IQueryService<SomeQuery, SomeDto> {
		@Override
		public SomeDto retrieve(final IQueryMessage<SomeQuery> message)
				throws KasperQueryException {
			final SomeQuery q = message.getQuery();

            if (q.isDoThrowSomeException()) {
                throw new KasperQueryException(q.aValue);
            }

			SomeDto dto = new SomeDto();
			dto.setQuery(q);
			return dto;
		}
	}

	public static class AccountDomain extends AbstractDomain { }

    // ------------------------------------------------------------------------

    @Override
    protected HttpQueryExposer createExposer(ApplicationContext ctx) {
        return new HttpQueryExposer(ctx.getBean(IPlatform.class), ctx.getBean(IQueryServicesLocator.class));
    }
	
	@Test
    public void testQueryRoundTrip() {
        // Given
		final SomeQuery query = new SomeQuery();
		query.aValue = "foo";
		query.doThrowSomeException = false;
		query.intArray = new int[]{1, 2, 3};

        // When
		final SomeDto dto = client().query(query, SomeDto.class);

        // Then
		assertEquals(query.aValue, dto.query.aValue);
		assertEquals(query.doThrowSomeException, dto.query.doThrowSomeException);
		assertArrayEquals(query.intArray, dto.query.intArray);
	}

    // ------------------------------------------------------------------------
	
	@Test(expected=KasperQueryException.class)
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

}
