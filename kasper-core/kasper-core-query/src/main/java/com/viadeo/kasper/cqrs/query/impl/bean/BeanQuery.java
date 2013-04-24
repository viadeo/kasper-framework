// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl.bean;

import java.io.Serializable;
import java.net.URL;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 * A simple bean-specified query
 *
 * @param <BEAN> the specification bean
 */
public class BeanQuery<BEAN extends Serializable> implements IQuery {

	private static final long serialVersionUID = -7969969435469794707L;

	public static final int PARAMETER_BEAN_POSITION = 0;

	private BEAN bean;

	// ------------------------------------------------------------------------

	public BeanQuery() {
	}

	public BeanQuery(final BEAN bean) {
		this.bean = Preconditions.checkNotNull(bean);
	}

	public BeanQuery(final URL url) {
		// TODO
		throw new UnsupportedOperationException();
	}

	// ------------------------------------------------------------------------

	public Optional<BEAN> getBean() {
		return Optional.fromNullable(this.bean);
	}

	public void setBean(final BEAN bean) {
		this.bean = Preconditions.checkNotNull(bean);
	}

	// ------------------------------------------------------------------------

	/**
	 * @return a new instance of the query specification bean
	 */
	public BEAN bean() {
		@SuppressWarnings("unchecked") // Safe
		final Optional<Class<BEAN>> beanClass = 
				(Optional<Class<BEAN>>) 
					ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(), BeanQuery.class,	BeanQuery.PARAMETER_BEAN_POSITION);

		if (!beanClass.isPresent()) {
			throw new KasperQueryRuntimeException("BEAN type cannot by determined for " + this.getClass().getName());
		}

		try {
			return beanClass.get().newInstance();
		} catch (final InstantiationException e) {
			throw new KasperQueryRuntimeException("Unable to build simple bean " + beanClass.get().getName(), e);
		} catch (final IllegalAccessException e) {
			throw new KasperQueryRuntimeException("Unable to build simple bean " + beanClass.get().getName(), e);
		}
	}

}
