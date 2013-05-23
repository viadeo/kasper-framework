// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

import org.joda.time.DateTime;
import org.junit.Test;

import com.viadeo.kasper.client.exceptions.KasperClientException;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.query.exposition.ITypeAdapter;
import com.viadeo.kasper.query.exposition.QueryBuilder;
import com.viadeo.kasper.query.exposition.QueryParser;

public class KasperClientQueryCreationTest {

	private Date now = new Date();
	private DateTime dt = new DateTime();

	// ------------------------------------------------------------------------

	public static class CombinedQuery implements IQuery {
		private static final long serialVersionUID = 7214975580153615343L;
		private final MoreComplexQuery innerQuery;

		public CombinedQuery(final MoreComplexQuery innerQuery) {
			this.innerQuery = innerQuery;
		}

		public MoreComplexQuery getInnerQuery() {
			return this.innerQuery;
		}
	}

	public static class ComplexQuery implements IQuery {
		private static final long serialVersionUID = -5681649437512129142L;
		private final SomePojo complexPojo;

		public ComplexQuery(final SomePojo complexPojo) {
			this.complexPojo = complexPojo;
		}

		public SomePojo getComplexPojo() {
			return this.complexPojo;
		}
	}

	static class SomePojo {
		private final String value;

		public SomePojo(final String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public static class MoreComplexQuery implements IQuery {
		private static final long serialVersionUID = -1376553638435430921L;

		private final String aStr;
		private final int aPrimitiveInt;
		private final Integer aInteger;
		private final boolean aPrimitiveBoolean;
		private final Date aDate;
		private final DateTime[] multipleDates;

		public MoreComplexQuery(final String aStr, final int aPrimitiveInt,
				final Integer aInteger, final boolean aPrimitiveBoolean,
				final Date aDate, final DateTime[] multipleDates) {
			super();
			this.aStr = aStr;
			this.aPrimitiveInt = aPrimitiveInt;
			this.aInteger = aInteger;
			this.aPrimitiveBoolean = aPrimitiveBoolean;
			this.aDate = aDate;
			this.multipleDates = multipleDates;
		}

		public String getaStr() {
			return this.aStr;
		}

		public int getaPrimitiveInt() {
			return this.aPrimitiveInt;
		}

		public Integer getaInteger() {
			return this.aInteger;
		}

		public boolean isaPrimitiveBoolean() {
			return this.aPrimitiveBoolean;
		}

		public Date getaDate() {
			return this.aDate;
		}

		public DateTime[] getMultipleDates() {
			return this.multipleDates;
		}
	}

	// ------------------------------------------------------------------------

	private MoreComplexQuery prepareMoreComplexQueryBean() {
		return new MoreComplexQuery("foo", 1, Integer.valueOf(2), true, now,
				new DateTime[] { dt, dt });
	}

	private void assertMoreComplexQueryContent(
			MultivaluedMap<String, String> queryParams) {
		assertEquals("foo", queryParams.getFirst("aStr"));
		assertEquals("1", queryParams.getFirst("aPrimitiveInt"));
		assertEquals("2", queryParams.getFirst("aInteger"));
		assertEquals("true", queryParams.getFirst("aPrimitiveBoolean"));
		assertEquals("" + now.getTime(), queryParams.getFirst("aDate"));
		assertEquals("" + dt.getMillis(),
				queryParams.get("multipleDates").get(0));
		assertEquals("" + dt.getMillis(),
				queryParams.get("multipleDates").get(0));
	}

	// ------------------------------------------------------------------------

	@Test(expected = KasperClientException.class)
	public void testThrowExceptionForComplexPojos() {

		// Given
		final KasperClient client = new KasperClient();
		final SomePojo pojo = new SomePojo("foo bar");
		final ComplexQuery query = new ComplexQuery(pojo);

		// When
		client.prepareQueryParams(query);

		// Then throw KasperClientException
	}

	// --

	@Test
	public void testComplexPojosWithCustomAdapter() {

		// Given
		final KasperClient client = new KasperClientBuilder().use(
				new ITypeAdapter<SomePojo>() {
					@Override
					public void adapt(final SomePojo value,
							final QueryBuilder builder) {
						builder.addSingle("somePojo", 2);
						builder.addSingle("otherProperty", true);
					}

					@Override
					public SomePojo adapt(QueryParser parser) {
						return null;
					}
				}).create();

		// When
		final MultivaluedMap<String, String> map = client
				.prepareQueryParams(new ComplexQuery(new SomePojo("foo bar")));

		// Then
		assertEquals("2", map.getFirst("somePojo"));

		// When
		final MultivaluedMap<String, String> map2 = client
				.prepareQueryParams(new ComplexQuery(new SomePojo("foo bar")));

		// Then
		assertEquals("true", map2.getFirst("otherProperty"));
		assertEquals(map, map2);
	}

	// --

	@Test
	public void testPrepareQueryParamsWithCombinedQueries() {

		// Given
		final KasperClient client = new KasperClient();
		final CombinedQuery query = new CombinedQuery(
				prepareMoreComplexQueryBean());

		// When
		final MultivaluedMap<String, String> map = client
				.prepareQueryParams(query);

		// Then
		assertMoreComplexQueryContent(map);
	}

	// --

	@Test
	public void testPrepareQueryParamsWithComplexObject() {

		// Given
		final KasperClient client = new KasperClient();
		final MoreComplexQuery query = prepareMoreComplexQueryBean();

		// When
		final MultivaluedMap<String, String> map = client
				.prepareQueryParams(query);

		// Then
		assertMoreComplexQueryContent(map);
	}

}
