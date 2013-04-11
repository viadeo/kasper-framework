package com.viadeo.kasper.test.cqrs.query.base;

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
import junit.framework.Assert;
import junit.framework.TestCase;

public class BaseQueryFilterElementTest extends TestCase {

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

	public void testValue() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<DQOTest, String>();
		Assert.assertFalse(eltFilter.getValue().isPresent());

		Assert.assertEquals(eltFilter.value("value").getValue().get(), "value");

		try {
			eltFilter.value(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}
	}

	// ------------------------------------------------------------------------

	public void testValueHelpers() {
		final BaseQueryFilterElement<DQOTest, String> eltFilter = new BaseQueryFilterElement<DQOTest, String>();

		// - EQUAL
		Assert.assertEquals(eltFilter.equal("value").getOperator().get(), BaseFilterOperator.EQ);
		Assert.assertEquals(eltFilter.equal("value").getValue().get(), "value");

		try {
			eltFilter.equal(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - NOTEQUAL
		Assert.assertEquals(eltFilter.notEqual("value").getOperator().get(), BaseFilterOperator.NE);
		Assert.assertEquals(eltFilter.notEqual("value").getValue().get(), "value");

		try {
			eltFilter.notEqual(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - GREATERTHAN
		Assert.assertEquals(eltFilter.greaterThan("value").getOperator().get(), BaseFilterOperator.GT);
		Assert.assertEquals(eltFilter.greaterThan("value").getValue().get(), "value");

		try {
			eltFilter.greaterThan(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - GREATERTHANOREQUALS
		Assert.assertEquals(eltFilter.gtOrEqual("value").getOperator().get(), BaseFilterOperator.GE);
		Assert.assertEquals(eltFilter.gtOrEqual("value").getValue().get(), "value");

		try {
			eltFilter.gtOrEqual(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - LESSTHAN
		Assert.assertEquals(eltFilter.lessThan("value").getOperator().get(), BaseFilterOperator.LT);
		Assert.assertEquals(eltFilter.lessThan("value").getValue().get(), "value");

		try {
			eltFilter.lessThan(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - LESSTHANOREQUALS
		Assert.assertEquals(eltFilter.ltOrEqual("value").getOperator().get(), BaseFilterOperator.LE);
		Assert.assertEquals(eltFilter.ltOrEqual("value").getValue().get(), "value");

		try {
			eltFilter.ltOrEqual(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - CONTAINS
		Assert.assertEquals(eltFilter.contains("value").getOperator().get(), BaseFilterOperator.CONTAINS);
		Assert.assertEquals(eltFilter.contains("value").getValue().get(), "value");

		try {
			eltFilter.contains(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - ENDSWITH
		Assert.assertEquals(eltFilter.endsWith("value").getOperator().get(), BaseFilterOperator.ENDSWITH);
		Assert.assertEquals(eltFilter.endsWith("value").getValue().get(), "value");

		try {
			eltFilter.endsWith(null);
			Assert.fail();
		} catch (final NullPointerException e) {
			// Ignore
		}

		// - STARTSWITH
		Assert.assertEquals(eltFilter.startsWith("value").getOperator().get(), BaseFilterOperator.STARTSWITH);
		Assert.assertEquals(eltFilter.startsWith("value").getValue().get(), "value");

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

		String stringField;
		Integer intField;

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
			stringFilter.isSatisfiedBy("value");
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		stringFilter.op(BaseFilterOperator.EQ);

		// Value has not been defined
		try {
			stringFilter.isSatisfiedBy("value");
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
			intFilter.isSatisfiedBy(42);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		intFilter.field(dqo.intField).op(BaseFilterOperator.STARTSWITH);
		try {
			intFilter.isSatisfiedBy(42);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

		intFilter.field(dqo.intField).op(BaseFilterOperator.ENDSWITH);
		try {
			intFilter.isSatisfiedBy(42);
			Assert.fail();
		} catch (final KasperQueryRuntimeException e) {
			// Ignore
		}

	}

	// ------------------------------------------------------------------------

	public void testSatisfaction() {

		final BaseQueryFilterElement<DQOTest, String> stringFilter = new BaseQueryFilterElement<DQOTest, String>();
		final BaseQueryFilterElement<DQOTest, Integer> intFilter = new BaseQueryFilterElement<DQOTest, Integer>();

		Assert.assertTrue(stringFilter.equal("value").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.equal("value").isSatisfiedBy("anotherValue"));

		Assert.assertTrue(intFilter.equal(42).isSatisfiedBy(42));
		Assert.assertFalse(intFilter.equal(42).isSatisfiedBy(24));

		Assert.assertFalse(stringFilter.notEqual("value").isSatisfiedBy("value"));
		Assert.assertTrue(stringFilter.notEqual("value").isSatisfiedBy("anotherValue"));

		Assert.assertFalse(intFilter.notEqual(42).isSatisfiedBy(42));
		Assert.assertTrue(intFilter.notEqual(42).isSatisfiedBy(24));

		Assert.assertTrue(stringFilter.lessThan("valuelong").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.lessThan("value").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.lessThan("value").isSatisfiedBy("valuelong"));

		Assert.assertTrue(intFilter.lessThan(42).isSatisfiedBy(24));
		Assert.assertFalse(intFilter.lessThan(24).isSatisfiedBy(42));

		Assert.assertFalse(stringFilter.greaterThan("valuelong").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.greaterThan("value").isSatisfiedBy("value"));
		Assert.assertTrue(stringFilter.greaterThan("value").isSatisfiedBy("valuelong"));

		Assert.assertFalse(intFilter.greaterThan(42).isSatisfiedBy(24));
		Assert.assertTrue(intFilter.greaterThan(24).isSatisfiedBy(42));

		Assert.assertTrue(stringFilter.gtOrEqual("value").isSatisfiedBy("value"));
		Assert.assertTrue(stringFilter.gtOrEqual("value").isSatisfiedBy("valuelong"));
		Assert.assertFalse(stringFilter.gtOrEqual("valuelong").isSatisfiedBy("value"));

		Assert.assertTrue(intFilter.gtOrEqual(24).isSatisfiedBy(24));
		Assert.assertTrue(intFilter.gtOrEqual(24).isSatisfiedBy(42));
		Assert.assertFalse(intFilter.gtOrEqual(42).isSatisfiedBy(24));

		Assert.assertTrue(stringFilter.ltOrEqual("value").isSatisfiedBy("value"));
		Assert.assertTrue(stringFilter.ltOrEqual("valuelong").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.ltOrEqual("value").isSatisfiedBy("valuelong"));

		Assert.assertTrue(intFilter.ltOrEqual(24).isSatisfiedBy(24));
		Assert.assertTrue(intFilter.ltOrEqual(42).isSatisfiedBy(24));
		Assert.assertFalse(intFilter.ltOrEqual(24).isSatisfiedBy(42));

		Assert.assertTrue(stringFilter.contains("value").isSatisfiedBy("ending with value"));
		Assert.assertTrue(stringFilter.contains("value").isSatisfiedBy("value starts"));
		Assert.assertTrue(stringFilter.contains("value").isSatisfiedBy("value"));
		Assert.assertTrue(stringFilter.contains("value").isSatisfiedBy("great value enclosed"));
		Assert.assertFalse(stringFilter.contains("value").isSatisfiedBy("val"));

		Assert.assertFalse(stringFilter.startsWith("value").isSatisfiedBy("ending with value"));
		Assert.assertTrue(stringFilter.startsWith("value").isSatisfiedBy("value starts"));
		Assert.assertTrue(stringFilter.startsWith("value").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.startsWith("value").isSatisfiedBy("great value enclosed"));
		Assert.assertFalse(stringFilter.startsWith("value").isSatisfiedBy("val"));

		Assert.assertTrue(stringFilter.endsWith("value").isSatisfiedBy("ending with value"));
		Assert.assertFalse(stringFilter.endsWith("value").isSatisfiedBy("value starts"));
		Assert.assertTrue(stringFilter.endsWith("value").isSatisfiedBy("value"));
		Assert.assertFalse(stringFilter.endsWith("value").isSatisfiedBy("great value enclosed"));
		Assert.assertFalse(stringFilter.endsWith("value").isSatisfiedBy("val"));
	}

}
