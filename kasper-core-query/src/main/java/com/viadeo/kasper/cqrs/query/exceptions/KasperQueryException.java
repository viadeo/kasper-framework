// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.exceptions;

import com.viadeo.kasper.exception.KasperException;

/**
 * 
 *         The base Kasper query exception
 * 
 */
public class KasperQueryException extends KasperException {

	private static final long serialVersionUID = 4331393973984839301L;

	// ------------------------------------------------------------------------

	public KasperQueryException(final String message, final Throwable e) {
		super(message, e);
	}

	public KasperQueryException(final String message) {
		super(message);
	}

}
