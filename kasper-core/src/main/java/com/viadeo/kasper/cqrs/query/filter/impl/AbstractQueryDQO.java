// ============================================================================
//				 KASPER - Kasper is the treasure keeper
//	www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//		   Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.cqrs.query.filter.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;
import com.viadeo.kasper.cqrs.query.filter.IQueryField;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 *
 *         A base implementation for IQueryDQO
 *
 * @param <DQO> itself for accurate link with filter
 *
 * @see QueryDQOFactory
 * @see IQueryDQO
 */
public abstract class AbstractQueryDQO<DQO extends AbstractQueryDQO<DQO>> implements IQueryDQO<DQO> {

	private static final long serialVersionUID = -6878704922450778336L;

	// ------------------------------------------------------------------------

	protected AbstractQueryDQO() {

	}

	// ------------------------------------------------------------------------

	/**
	 * Non-optimized instance initialization
	 * DQO objects are not made to be multi-instantiated
	 * QueryDQOFactory stores single instances
	 */
	@Override
	public void init() {
		@SuppressWarnings("unchecked")
		// Safe
		final Class<? extends AbstractQueryDQO<DQO>> thisClass = (Class<? extends AbstractQueryDQO<DQO>>) this.getClass();

		for (final Field field : thisClass.getDeclaredFields()) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			if (Modifier.isPublic(field.getModifiers()) && IQueryField.class.isAssignableFrom(field.getType())) {
				try {

					@SuppressWarnings("rawtypes")
					final Optional<Class> optFieldClass = ReflectionGenericsResolver.getClass(field.getGenericType());
					if (!optFieldClass.isPresent()) {
						throw new KasperQueryRuntimeException(String.format("Unable to find field class for %s", field.getName()));
					}
					final Class<?> fieldClass = optFieldClass.get();

					@SuppressWarnings("unchecked") // safe
					final IQueryField<?,DQO, ?> newField = (IQueryField<?, DQO, ?>) fieldClass.newInstance();
					newField.setName(field.getName());
					this.configureField(field, newField);
					field.set(this, newField);

				} catch (final InstantiationException e) {
					throw new KasperQueryRuntimeException(String.format("Unable to instanciate field %s on DQO %s", field.getName(), thisClass.getSimpleName()), e);
				} catch (final IllegalAccessException e) {
					throw new KasperQueryRuntimeException(String.format("Unable to instanciate field %s on DQO %s", field.getName(), thisClass.getSimpleName()), e);
				}
			}
		}
	}

	// ------

	/**
	 * Delegation method Children can override it to make some specific
	 * configuration on fields
	 *
	 * @param field
	 * @param fieldInstance
	 */
	protected void configureField(final Field field, final IQueryField<?, DQO, ?> fieldInstance) {
		// Can be overriden by child classes to configure own fields
	}

}