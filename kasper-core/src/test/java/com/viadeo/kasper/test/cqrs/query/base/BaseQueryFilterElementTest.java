package com.viadeo.kasper.test.cqrs.query.base;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.filter.IQueryField;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseFilterOperator;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryFilterElement;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class BaseQueryFilterElementTest extends TestCase {

	private static final int QUARANTE_DEUX = 42;
	private static final int VINGT_QUATRE = 24;
	private static final String VALUE = "value";
	private static final String VALUE_LONG = "valuelong";
	
	@Test
	public void testConstruct() {

		new BaseQueryFilterElement<DQOTest, String>();

		try {
			@SuppressWarnings("unused")
			final BaseQueryFilterElement<DQOTest, String> eltFilter2 = new BaseQueryFilterElement<>(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		@SuppressWarnings("unchecked")
		final IQueryField<String, DQOTest, BaseQueryFilterElement<DQOTest, String>> field = Mockito.mock(IQueryField.class);

		final BaseQueryFilterElement<DQOTest, String> eltFilter3 = new BaseQueryFilterElement<>(field);

		assertEquals(eltFilter3.getField().get(), field);
	}

	// ------------------------------------------------------------------------

	@Test
	public void testField() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<>();
		assertFalse(eltFilter.getField().isPresent());

		@SuppressWarnings("unchecked")
		final IQueryField<String, DQOTest, BaseQueryFilterElement<DQOTest, String>> field = Mockito.mock(IQueryField.class);

		assertEquals(eltFilter.field(field).getField().get(), field);

		try {
			eltFilter.field(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testOperator() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<>();
		assertFalse(eltFilter.getOperator().isPresent());

		for (final BaseFilterOperator op : BaseFilterOperator.values()) {
			assertEquals(eltFilter.op(op).getOperator().get(), op);
		}

		assertEquals(eltFilter.equal().getOperator().get(), BaseFilterOperator.EQ);
		assertEquals(eltFilter.notEqual().getOperator().get(), BaseFilterOperator.NE);
		assertEquals(eltFilter.lessThan().getOperator().get(), BaseFilterOperator.LT);
		assertEquals(eltFilter.greaterThan().getOperator().get(), BaseFilterOperator.GT);
		assertEquals(eltFilter.ltOrEqual().getOperator().get(), BaseFilterOperator.LE);
		assertEquals(eltFilter.gtOrEqual().getOperator().get(), BaseFilterOperator.GE);
		assertEquals(eltFilter.contains().getOperator().get(), BaseFilterOperator.CONTAINS);
		assertEquals(eltFilter.startsWith().getOperator().get(), BaseFilterOperator.STARTSWITH);
		assertEquals(eltFilter.endsWith().getOperator().get(), BaseFilterOperator.ENDSWITH);

		try {
			eltFilter.op(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

	}

	// ------------------------------------------------------------------------

	@Test
	public void testValue() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<>();
		assertFalse(eltFilter.getValue().isPresent());

		assertEquals(eltFilter.value(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.value(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testValueHelpers() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<>();

		// - EQUAL
		assertEquals(eltFilter.equal(VALUE).getOperator().get(), BaseFilterOperator.EQ);
		assertEquals(eltFilter.equal(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.equal(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - NOTEQUAL
		assertEquals(eltFilter.notEqual(VALUE).getOperator().get(), BaseFilterOperator.NE);
		assertEquals(eltFilter.notEqual(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.notEqual(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - GREATERTHAN
		assertEquals(eltFilter.greaterThan(VALUE).getOperator().get(), BaseFilterOperator.GT);
		assertEquals(eltFilter.greaterThan(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.greaterThan(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - GREATERTHANOREQUALS
		assertEquals(eltFilter.gtOrEqual(VALUE).getOperator().get(), BaseFilterOperator.GE);
		assertEquals(eltFilter.gtOrEqual(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.gtOrEqual(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - LESSTHAN
		assertEquals(eltFilter.lessThan(VALUE).getOperator().get(), BaseFilterOperator.LT);
		assertEquals(eltFilter.lessThan(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.lessThan(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - LESSTHANOREQUALS
		assertEquals(eltFilter.ltOrEqual(VALUE).getOperator().get(), BaseFilterOperator.LE);
		assertEquals(eltFilter.ltOrEqual(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.ltOrEqual(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - CONTAINS
		assertEquals(eltFilter.contains(VALUE).getOperator().get(), BaseFilterOperator.CONTAINS);
		assertEquals(eltFilter.contains(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.contains(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - ENDSWITH
		assertEquals(eltFilter.endsWith(VALUE).getOperator().get(), BaseFilterOperator.ENDSWITH);
		assertEquals(eltFilter.endsWith(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.endsWith(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - STARTSWITH
		assertEquals(eltFilter.startsWith(VALUE).getOperator().get(), BaseFilterOperator.STARTSWITH);
		assertEquals(eltFilter.startsWith(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.startsWith(null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

	}

	// ------------------------------------------------------------------------
	// BASIC DQO/DTO stubbed implementations for testing purposes
	// ------------------------------------------------------------------------

	private static final class dtofield implements IQueryDTO {
		private static final long serialVersionUID = 4000122334767301613L;

		private String stringField;
		private Integer intField;

	}

	private static final class dqofield extends AbstractQueryDQO<dqofield> {

		private static final long serialVersionUID = -6031139541221227916L;

		@SuppressWarnings("unchecked")
		public final IQueryField<String, dqofield, BaseQueryFilterElement<dqofield, String>> stringField = Mockito.mock(IQueryField.class);

		@SuppressWarnings("unchecked")
		public final IQueryField<Integer, dqofield, BaseQueryFilterElement<dqofield, Integer>> intField = Mockito.mock(IQueryField.class);

		@SuppressWarnings("unchecked")
		// Field that will be absent from DTO
		public final IQueryField<String, dqofield, BaseQueryFilterElement<dqofield, String>> noneField = Mockito.mock(IQueryField.class);

		// Init mocks
		dqofield(final dtofield dto) {
			Mockito.when(this.stringField.getFieldValue(dto)).thenAnswer(
					new Answer<Optional<String>>() {
						@Override
						public Optional<String> answer(final InvocationOnMock invocation) throws Throwable {
							return Optional.fromNullable(dto.stringField);
						}
					});
			Mockito.when(this.intField.getFieldValue(dto)).thenAnswer(
					new Answer<Optional<Integer>>() {
						@Override
						public Optional<Integer> answer(final InvocationOnMock invocation) throws Throwable {
							return Optional.fromNullable(dto.intField);
						}
					});
			Mockito.when(this.noneField.getFieldValue(dto)).thenReturn(Optional.fromNullable((String) null));
		}

	}

	// ------------------------------------------------------------------------

	@Test
	public void testSatisfactionErrors() {

		final BaseQueryFilterElement<dqofield, String> stringFilter = new BaseQueryFilterElement<>();

		final BaseQueryFilterElement<dqofield, Integer> intFilter = new BaseQueryFilterElement<>();

		// NULL values

		try {
			stringFilter.isSatisfiedBy((dtofield) null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		try {
			stringFilter.isSatisfiedBy((String) null);
			fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// Field has not been defined for DTO access
		final dtofield dto = new dtofield();
		try {
			stringFilter.isSatisfiedBy(dto);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		// Operator has not been defined
		try {
			stringFilter.isSatisfiedBy(VALUE);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		stringFilter.op(BaseFilterOperator.EQ);

		// Value has not been defined
		try {
			stringFilter.isSatisfiedBy(VALUE);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		final dqofield dqo = new dqofield(dto);

		// Field is defined but unexistent in provided DTO
		stringFilter.field(dqo.noneField);
		try {
			stringFilter.isSatisfiedBy(dto);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		// Field is defined but already null in provided DTO
		stringFilter.field(dqo.stringField);
		try {
			stringFilter.isSatisfiedBy(dto);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		// Trying to use string-only operators on non-string values
		intFilter.field(dqo.intField).op(BaseFilterOperator.CONTAINS);
		try {
			intFilter.isSatisfiedBy(QUARANTE_DEUX);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		intFilter.field(dqo.intField).op(BaseFilterOperator.STARTSWITH);
		try {
			intFilter.isSatisfiedBy(QUARANTE_DEUX);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

		intFilter.field(dqo.intField).op(BaseFilterOperator.ENDSWITH);
		try {
			intFilter.isSatisfiedBy(QUARANTE_DEUX);
			fail();
		} catch (final KasperQueryException e) {
			// Ignore
		}

	}

	// ------------------------------------------------------------------------

	@Test
	public void testSatisfaction() {

		final BaseQueryFilterElement<DQOTest, String> stringFilter = new BaseQueryFilterElement<>();
		final BaseQueryFilterElement<DQOTest, Integer> intFilter = new BaseQueryFilterElement<>();

		assertTrue(stringFilter.equal(VALUE).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.equal(VALUE).isSatisfiedBy("anotherValue"));

		assertTrue(intFilter.equal(QUARANTE_DEUX).isSatisfiedBy(QUARANTE_DEUX));
		assertFalse(intFilter.equal(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));

		assertFalse(stringFilter.notEqual(VALUE).isSatisfiedBy(VALUE));
		assertTrue(stringFilter.notEqual(VALUE).isSatisfiedBy("anotherValue"));

		assertFalse(intFilter.notEqual(QUARANTE_DEUX).isSatisfiedBy(QUARANTE_DEUX));
		assertTrue(intFilter.notEqual(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));

		assertTrue(stringFilter.lessThan(VALUE_LONG).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.lessThan(VALUE).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.lessThan(VALUE).isSatisfiedBy(VALUE_LONG));

		assertTrue(intFilter.lessThan(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));
		assertFalse(intFilter.lessThan(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));

		assertFalse(stringFilter.greaterThan(VALUE_LONG).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.greaterThan(VALUE).isSatisfiedBy(VALUE));
		assertTrue(stringFilter.greaterThan(VALUE).isSatisfiedBy(VALUE_LONG));

		assertFalse(intFilter.greaterThan(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));
		assertTrue(intFilter.greaterThan(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));

		assertTrue(stringFilter.gtOrEqual(VALUE).isSatisfiedBy(VALUE));
		assertTrue(stringFilter.gtOrEqual(VALUE).isSatisfiedBy(VALUE_LONG));
		assertFalse(stringFilter.gtOrEqual(VALUE_LONG).isSatisfiedBy(VALUE));

		assertTrue(intFilter.gtOrEqual(VINGT_QUATRE).isSatisfiedBy(VINGT_QUATRE));
		assertTrue(intFilter.gtOrEqual(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));
		assertFalse(intFilter.gtOrEqual(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));

		assertTrue(stringFilter.ltOrEqual(VALUE).isSatisfiedBy(VALUE));
		assertTrue(stringFilter.ltOrEqual(VALUE_LONG).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.ltOrEqual(VALUE).isSatisfiedBy(VALUE_LONG));

		assertTrue(intFilter.ltOrEqual(VINGT_QUATRE).isSatisfiedBy(VINGT_QUATRE));
		assertTrue(intFilter.ltOrEqual(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));
		assertFalse(intFilter.ltOrEqual(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));

		assertTrue(stringFilter.contains(VALUE).isSatisfiedBy("ending with " + VALUE));
	assertTrue(stringFilter.contains(VALUE).isSatisfiedBy(VALUE + " starts"));
		assertTrue(stringFilter.contains(VALUE).isSatisfiedBy(VALUE));
		assertTrue(stringFilter.contains(VALUE).isSatisfiedBy("great " + VALUE + " enclosed"));
		assertFalse(stringFilter.contains(VALUE).isSatisfiedBy("val"));

		assertFalse(stringFilter.startsWith(VALUE).isSatisfiedBy("ending with " + VALUE));
		assertTrue(stringFilter.startsWith(VALUE).isSatisfiedBy(VALUE + " starts"));
		assertTrue(stringFilter.startsWith(VALUE).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.startsWith(VALUE).isSatisfiedBy("great " + VALUE + " enclosed"));
		assertFalse(stringFilter.startsWith(VALUE).isSatisfiedBy("val"));

		assertTrue(stringFilter.endsWith(VALUE).isSatisfiedBy("ending with " + VALUE));
		assertFalse(stringFilter.endsWith(VALUE).isSatisfiedBy(VALUE + " starts"));
		assertTrue(stringFilter.endsWith(VALUE).isSatisfiedBy(VALUE));
		assertFalse(stringFilter.endsWith(VALUE).isSatisfiedBy("great " + VALUE + " enclosed"));
		assertFalse(stringFilter.endsWith(VALUE).isSatisfiedBy("val"));
	}

}
