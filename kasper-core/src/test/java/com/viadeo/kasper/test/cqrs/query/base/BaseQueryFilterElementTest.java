package com.viadeo.kasper.test.cqrs.query.base;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.cqrs.query.filter.IQueryField;
import com.viadeo.kasper.cqrs.query.filter.impl.AbstractQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseFilterOperator;
import com.viadeo.kasper.cqrs.query.filter.impl.base.BaseQueryFilterElement;
import com.viadeo.kasper.test.cqrs.query.StubbedDQTOS.DQOTest;

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
			final BaseQueryFilterElement<DQOTest, String> eltFilter2 = new BaseQueryFilterElement<DQOTest, String>(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		@SuppressWarnings("unchecked")
		final IQueryField<String, DQOTest, BaseQueryFilterElement<DQOTest, String>> field = Mockito.mock(IQueryField.class);

		final BaseQueryFilterElement<DQOTest, String> eltFilter3 = new BaseQueryFilterElement<DQOTest, String>(field);

		Assert.assertEquals(eltFilter3.getField().get(), field);
	}

	// ------------------------------------------------------------------------

	@Test
	public void testField() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<DQOTest, String>();
		Assert.assertFalse(eltFilter.getField().isPresent());

		@SuppressWarnings("unchecked")
		final IQueryField<String, DQOTest, BaseQueryFilterElement<DQOTest, String>> field = Mockito.mock(IQueryField.class);

		Assert.assertEquals(eltFilter.field(field).getField().get(), field);

		try {
			eltFilter.field(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testOperator() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<DQOTest, String>();
		Assert.assertFalse(eltFilter.getOperator().isPresent());

		for (final BaseFilterOperator op : BaseFilterOperator.values()) {
			Assert.assertEquals(eltFilter.op(op).getOperator().get(), op);
		}

		Assert.assertEquals(eltFilter.equal().getOperator().get(), BaseFilterOperator.EQ);
		Assert.assertEquals(eltFilter.notEqual().getOperator().get(), BaseFilterOperator.NE);
		Assert.assertEquals(eltFilter.lessThan().getOperator().get(), BaseFilterOperator.LT);
		Assert.assertEquals(eltFilter.greaterThan().getOperator().get(), BaseFilterOperator.GT);
		Assert.assertEquals(eltFilter.ltOrEqual().getOperator().get(), BaseFilterOperator.LE);
		Assert.assertEquals(eltFilter.gtOrEqual().getOperator().get(), BaseFilterOperator.GE);
		Assert.assertEquals(eltFilter.contains().getOperator().get(), BaseFilterOperator.CONTAINS);
		Assert.assertEquals(eltFilter.startsWith().getOperator().get(), BaseFilterOperator.STARTSWITH);
		Assert.assertEquals(eltFilter.endsWith().getOperator().get(), BaseFilterOperator.ENDSWITH);

		try {
			eltFilter.op(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

	}

	// ------------------------------------------------------------------------

	@Test
	public void testValue() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<DQOTest, String>();
		Assert.assertFalse(eltFilter.getValue().isPresent());

		Assert.assertEquals(eltFilter.value(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.value(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	// ------------------------------------------------------------------------

	@Test
	public void testValueHelpers() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<DQOTest, String>();

		// - EQUAL
		Assert.assertEquals(eltFilter.equal(VALUE).getOperator().get(), BaseFilterOperator.EQ);
		Assert.assertEquals(eltFilter.equal(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.equal(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - NOTEQUAL
		Assert.assertEquals(eltFilter.notEqual(VALUE).getOperator().get(), BaseFilterOperator.NE);
		Assert.assertEquals(eltFilter.notEqual(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.notEqual(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - GREATERTHAN
		Assert.assertEquals(eltFilter.greaterThan(VALUE).getOperator().get(), BaseFilterOperator.GT);
		Assert.assertEquals(eltFilter.greaterThan(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.greaterThan(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - GREATERTHANOREQUALS
		Assert.assertEquals(eltFilter.gtOrEqual(VALUE).getOperator().get(), BaseFilterOperator.GE);
		Assert.assertEquals(eltFilter.gtOrEqual(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.gtOrEqual(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - LESSTHAN
		Assert.assertEquals(eltFilter.lessThan(VALUE).getOperator().get(), BaseFilterOperator.LT);
		Assert.assertEquals(eltFilter.lessThan(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.lessThan(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - LESSTHANOREQUALS
		Assert.assertEquals(eltFilter.ltOrEqual(VALUE).getOperator().get(), BaseFilterOperator.LE);
		Assert.assertEquals(eltFilter.ltOrEqual(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.ltOrEqual(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - CONTAINS
		Assert.assertEquals(eltFilter.contains(VALUE).getOperator().get(), BaseFilterOperator.CONTAINS);
		Assert.assertEquals(eltFilter.contains(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.contains(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - ENDSWITH
		Assert.assertEquals(eltFilter.endsWith(VALUE).getOperator().get(), BaseFilterOperator.ENDSWITH);
		Assert.assertEquals(eltFilter.endsWith(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.endsWith(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - STARTSWITH
		Assert.assertEquals(eltFilter.startsWith(VALUE).getOperator().get(), BaseFilterOperator.STARTSWITH);
		Assert.assertEquals(eltFilter.startsWith(VALUE).getValue().get(), VALUE);

		try {
			eltFilter.startsWith(null);
			Assert.fail();
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

		final BaseQueryFilterElement<dqofield, String> stringFilter = new BaseQueryFilterElement<dqofield, String>();

		final BaseQueryFilterElement<dqofield, Integer> intFilter = new BaseQueryFilterElement<dqofield, Integer>();

		// NULL values

		try {
			stringFilter.isSatisfiedBy((dtofield) null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		try {
			stringFilter.isSatisfiedBy((String) null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// Field has not been defined for DTO access
		final dtofield dto = new dtofield();
		try {
			stringFilter.isSatisfiedBy(dto);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		// Operator has not been defined
		try {
			stringFilter.isSatisfiedBy(VALUE);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		stringFilter.op(BaseFilterOperator.EQ);

		// Value has not been defined
		try {
			stringFilter.isSatisfiedBy(VALUE);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		final dqofield dqo = new dqofield(dto);

		// Field is defined but unexistent in provided DTO
		stringFilter.field(dqo.noneField);
		try {
			stringFilter.isSatisfiedBy(dto);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		// Field is defined but already null in provided DTO
		stringFilter.field(dqo.stringField);
		try {
			stringFilter.isSatisfiedBy(dto);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		// Trying to use string-only operators on non-string values
		intFilter.field(dqo.intField).op(BaseFilterOperator.CONTAINS);
		try {
			intFilter.isSatisfiedBy(QUARANTE_DEUX);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		intFilter.field(dqo.intField).op(BaseFilterOperator.STARTSWITH);
		try {
			intFilter.isSatisfiedBy(QUARANTE_DEUX);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		intFilter.field(dqo.intField).op(BaseFilterOperator.ENDSWITH);
		try {
			intFilter.isSatisfiedBy(QUARANTE_DEUX);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

	}

	// ------------------------------------------------------------------------

	@Test
	public void testSatisfaction() {

		final BaseQueryFilterElement<DQOTest, String> stringFilter = new BaseQueryFilterElement<DQOTest, String>();
		final BaseQueryFilterElement<DQOTest, Integer> intFilter = new BaseQueryFilterElement<DQOTest, Integer>();

		Assert.assertTrue(stringFilter.equal(VALUE).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.equal(VALUE).isSatisfiedBy("anotherValue"));

		Assert.assertTrue(intFilter.equal(QUARANTE_DEUX).isSatisfiedBy(QUARANTE_DEUX));
		Assert.assertFalse(intFilter.equal(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));

		Assert.assertFalse(stringFilter.notEqual(VALUE).isSatisfiedBy(VALUE));
		Assert.assertTrue(stringFilter.notEqual(VALUE).isSatisfiedBy("anotherValue"));

		Assert.assertFalse(intFilter.notEqual(QUARANTE_DEUX).isSatisfiedBy(QUARANTE_DEUX));
		Assert.assertTrue(intFilter.notEqual(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));

		Assert.assertTrue(stringFilter.lessThan(VALUE_LONG).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.lessThan(VALUE).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.lessThan(VALUE).isSatisfiedBy(VALUE_LONG));

		Assert.assertTrue(intFilter.lessThan(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));
		Assert.assertFalse(intFilter.lessThan(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));

		Assert.assertFalse(stringFilter.greaterThan(VALUE_LONG).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.greaterThan(VALUE).isSatisfiedBy(VALUE));
		Assert.assertTrue(stringFilter.greaterThan(VALUE).isSatisfiedBy(VALUE_LONG));

		Assert.assertFalse(intFilter.greaterThan(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));
		Assert.assertTrue(intFilter.greaterThan(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));

		Assert.assertTrue(stringFilter.gtOrEqual(VALUE).isSatisfiedBy(VALUE));
		Assert.assertTrue(stringFilter.gtOrEqual(VALUE).isSatisfiedBy(VALUE_LONG));
		Assert.assertFalse(stringFilter.gtOrEqual(VALUE_LONG).isSatisfiedBy(VALUE));

		Assert.assertTrue(intFilter.gtOrEqual(VINGT_QUATRE).isSatisfiedBy(VINGT_QUATRE));
		Assert.assertTrue(intFilter.gtOrEqual(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));
		Assert.assertFalse(intFilter.gtOrEqual(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));

		Assert.assertTrue(stringFilter.ltOrEqual(VALUE).isSatisfiedBy(VALUE));
		Assert.assertTrue(stringFilter.ltOrEqual(VALUE_LONG).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.ltOrEqual(VALUE).isSatisfiedBy(VALUE_LONG));

		Assert.assertTrue(intFilter.ltOrEqual(VINGT_QUATRE).isSatisfiedBy(VINGT_QUATRE));
		Assert.assertTrue(intFilter.ltOrEqual(QUARANTE_DEUX).isSatisfiedBy(VINGT_QUATRE));
		Assert.assertFalse(intFilter.ltOrEqual(VINGT_QUATRE).isSatisfiedBy(QUARANTE_DEUX));

		Assert.assertTrue(stringFilter.contains(VALUE).isSatisfiedBy("ending with " + VALUE));
	Assert.assertTrue(stringFilter.contains(VALUE).isSatisfiedBy(VALUE + " starts"));
		Assert.assertTrue(stringFilter.contains(VALUE).isSatisfiedBy(VALUE));
		Assert.assertTrue(stringFilter.contains(VALUE).isSatisfiedBy("great "+VALUE+" enclosed"));
		Assert.assertFalse(stringFilter.contains(VALUE).isSatisfiedBy("val"));

		Assert.assertFalse(stringFilter.startsWith(VALUE).isSatisfiedBy("ending with " + VALUE));
		Assert.assertTrue(stringFilter.startsWith(VALUE).isSatisfiedBy(VALUE + " starts"));
		Assert.assertTrue(stringFilter.startsWith(VALUE).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.startsWith(VALUE).isSatisfiedBy("great "+VALUE+" enclosed"));
		Assert.assertFalse(stringFilter.startsWith(VALUE).isSatisfiedBy("val"));

		Assert.assertTrue(stringFilter.endsWith(VALUE).isSatisfiedBy("ending with "+VALUE));
		Assert.assertFalse(stringFilter.endsWith(VALUE).isSatisfiedBy(VALUE + " starts"));
		Assert.assertTrue(stringFilter.endsWith(VALUE).isSatisfiedBy(VALUE));
		Assert.assertFalse(stringFilter.endsWith(VALUE).isSatisfiedBy("great "+VALUE+" enclosed"));
		Assert.assertFalse(stringFilter.endsWith(VALUE).isSatisfiedBy("val"));
	}

}
