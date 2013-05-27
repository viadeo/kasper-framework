package com.viadeo.kasper.exposition;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.ddd.impl.AbstractDomain;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import com.viadeo.kasper.platform.IPlatform;

public class HttpQueryExposerTest extends BaseHttpExposerTest<HttpQueryExposer> {
	
	public HttpQueryExposerTest() {
	}

    @Override
    protected HttpQueryExposer createExposer(ApplicationContext ctx) {
        return new HttpQueryExposer(ctx.getBean(IPlatform.class), ctx.getBean(IQueryServicesLocator.class));
    }
	
	@Test public void testQueryRoundTrip() {
		SomeQuery query = new SomeQuery();
		query.aValue = "foo";
		query.doThrowSomeException = false;
		query.intArray = new int[]{1, 2, 3};
		
		SomeDto dto = client().query(query, SomeDto.class);
		
		assertEquals(query.aValue, dto.query.aValue);
		assertEquals(query.doThrowSomeException, dto.query.doThrowSomeException);
		assertArrayEquals(query.intArray, dto.query.intArray);
	}
	
	@Test(expected=KasperClientException.class) public void testQueryServiceThrowingException() {
		SomeQuery query = new SomeQuery();
		query.doThrowSomeException = true;
		
		client().query(query, SomeDto.class);
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
		public void setIntArray(int[] intArray) {
			this.intArray = intArray;
		}
		public String getaValue() {
			return aValue;
		}
		public boolean isDoThrowSomeException() {
			return doThrowSomeException;
		}
		public void setaValue(String aValue) {
			this.aValue = aValue;
		}
		public void setDoThrowSomeException(boolean doThrowSomeException) {
			this.doThrowSomeException = doThrowSomeException;
		}
	}
	
	public static class SomeDto implements IQueryDTO {
		private static final long serialVersionUID = 4780302444624913577L;
		private SomeQuery query;

		public SomeQuery getQuery() {
			return query;
		}

		public void setQuery(SomeQuery query) {
			this.query = query;
		}
	}
	
	@XKasperQueryService(domain=AccountDomain.class)
	public static class SomeQueryService implements IQueryService<SomeQuery, SomeDto> {
		@Override
		public SomeDto retrieve(IQueryMessage<SomeQuery> message)
				throws KasperQueryException {
			final SomeQuery q = message.getQuery();
			if (q.isDoThrowSomeException()) throw new KasperQueryException(q.aValue);
			
			SomeDto dto = new SomeDto();
			dto.setQuery(q);
			return dto;
		}
	}
	
	public static class AccountDomain extends AbstractDomain {

	}
}
