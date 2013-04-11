// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryRuntimeException;
import com.viadeo.kasper.cqrs.query.filter.IQueryDQO;

/**
 * 
 *         Simple instance factory for DQOs DQOs base implementation use
 *         reflection to create fields These are description objects not
 *         targeted to be directly manipulated
 * 
 * @see IQueryDQO
 */
public class QueryDQOFactory {

	/**
	 * the DQO cache
	 */
	private static Map<Class<? extends IQueryDQO<?>>, IQueryDQO<?>> dqoCache;
	static {
		QueryDQOFactory.dqoCache = new ConcurrentHashMap<Class<? extends IQueryDQO<?>>, IQueryDQO<?>>();
	}

	// ------------------------------------------------------------------------

	/**
	 * Returns a shared instance of requested DQO class
	 * 
	 * @param dqoClass
	 * @return a cached DQO instance
	 */
	static <DQO extends IQueryDQO<?>> DQO get(final Class<DQO> dqoClass) {

		if (!QueryDQOFactory.dqoCache.containsKey(Preconditions
				.checkNotNull(dqoClass))) {
			try {

				final Constructor<?> constructor = dqoClass	.getDeclaredConstructor();
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}

				final IQueryDQO<?> newInstance = (IQueryDQO<?>) constructor	.newInstance();
				newInstance.init();
				QueryDQOFactory.dqoCache.put(dqoClass, newInstance);

			} catch (final InstantiationException e) {
				throw new KasperQueryRuntimeException(
						"Unable to instanciate new DQO instance", e);
			} catch (final IllegalAccessException e) {
				throw new KasperQueryRuntimeException(
						"Unable to instanciate new DQO instance", e);
			} catch (final SecurityException e) {
				throw new KasperQueryRuntimeException(
						"Unable to instanciate new DQO instance", e);
			} catch (final NoSuchMethodException e) {
				throw new KasperQueryRuntimeException(
						"Unable to instanciate new DQO instance", e);
			} catch (final IllegalArgumentException e) {
				throw new KasperQueryRuntimeException(
						"Unable to instanciate new DQO instance", e);
			} catch (final InvocationTargetException e) {
				throw new KasperQueryRuntimeException(
						"Unable to instanciate new DQO instance",
						e.getTargetException());
			}
		}

		@SuppressWarnings("unchecked") // Safe
		final DQO dqo = (DQO) QueryDQOFactory.dqoCache.get(dqoClass);
		return dqo;
	}

}
