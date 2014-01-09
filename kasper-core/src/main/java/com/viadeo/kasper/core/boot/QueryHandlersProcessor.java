// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Process Kasper query handlers dynamic registration at kasper platform boot
 * 
 * @see XKasperQueryHandler
 */
public class QueryHandlersProcessor extends SingletonAnnotationProcessor<XKasperQueryHandler, QueryHandler> {
	private static final Logger LOGGER = LoggerFactory.getLogger(QueryHandlersProcessor.class);

	/**
	 * The locator to register query handlers on
	 */
	private transient QueryHandlersLocator queryHandlersLocator;

	// ------------------------------------------------------------------------

	/**
	 * Process Kasper query handler
	 * 
	 * @see com.viadeo.kasper.cqrs.query.QueryHandler
	 * @see AnnotationProcessor#process(java.lang.Class)
	 */
	@Override
    @SuppressWarnings("unchecked")
	public void process(final Class queryHandlerClazz, final QueryHandler queryHandler) {
		LOGGER.info("Record on query handlers locator : " + queryHandlerClazz.getName());

		final String handlerName;
		final XKasperQueryHandler annotation = (XKasperQueryHandler)
                queryHandlerClazz.getAnnotation(XKasperQueryHandler.class);

		if (annotation.name().isEmpty()) {
			handlerName = queryHandlerClazz.getSimpleName();
		} else {
			handlerName = annotation.name();
		}

		//- Register the query handler to the locator -------------------------
		this.queryHandlersLocator.registerHandler(handlerName, queryHandler, annotation.domain());
	}

	// ------------------------------------------------------------------------

	/**
	 * @param queryHandlersLocator the locator to register query handlers on
	 */
	public void setQueryHandlersLocator(final QueryHandlersLocator queryHandlersLocator) {
		this.queryHandlersLocator = Preconditions.checkNotNull(queryHandlersLocator);
	}

}
