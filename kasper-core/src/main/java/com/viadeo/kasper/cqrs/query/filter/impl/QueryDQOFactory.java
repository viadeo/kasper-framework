// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.filter.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.filter.QueryDQO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 *         Simple instance factory for DQOs DQOs base implementation use
 *         reflection to create fields These are description objects not
 *         targeted to be directly manipulated
 * 
 * @see com.viadeo.kasper.cqrs.query.filter.QueryDQO
 */
public final class QueryDQOFactory {

	private static final String ERROR_INSTANCE = "Unable to instanciate new DQO instance";
	
	private QueryDQOFactory() { /* singleton */ }
	
	/**
	 * the DQO cache
	 */
	private static Map<Class<? extends QueryDQO<?>>, QueryDQO<?>> dqoCache;
	static {
		QueryDQOFactory.dqoCache = new ConcurrentHashMap<>();
	}

	// ------------------------------------------------------------------------

	/**
	 * Returns a shared instance of requested DQO class
	 * 
	 * @param dqoClass
	 * @return a cached DQO instance
	 */
	@SuppressWarnings("unchecked")
	public static <DQO extends QueryDQO<?>> DQO get(final Class<DQO> dqoClass) {

		if (!QueryDQOFactory.dqoCache.containsKey(Preconditions
				.checkNotNull(dqoClass))) {
			try {

				final Constructor<?> constructor = dqoClass	.getDeclaredConstructor();
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}

				final QueryDQO<?> newInstance = (QueryDQO<?>) constructor	.newInstance();
				newInstance.init();
				QueryDQOFactory.dqoCache.put(dqoClass, newInstance);

			} catch (final InstantiationException | IllegalAccessException |
                           SecurityException | NoSuchMethodException |
                           IllegalArgumentException e) {
				throw new KasperQueryException(ERROR_INSTANCE, e);
			} catch (final InvocationTargetException e) {
				throw new KasperQueryException(ERROR_INSTANCE, e.getTargetException());
			}
		}

		return (DQO) QueryDQOFactory.dqoCache.get(dqoClass);
	}

}
