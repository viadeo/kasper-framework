// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.exceptions;

import com.viadeo.kasper.exception.KasperRuntimeException;

/**
 * 
 *         The base Kasper query runtime exception
 * 
 */
public class KasperQueryRuntimeException extends KasperRuntimeException {

	private static final long serialVersionUID = -110263884498561991L;

	// ------------------------------------------------------------------------

	public KasperQueryRuntimeException(final String message, final Throwable t) {
		super(message, t);
	}

	public KasperQueryRuntimeException(final String message, final Exception e) {
		super(message, e);
	}

	public KasperQueryRuntimeException(final String message) {
		super(message);
	}

}
