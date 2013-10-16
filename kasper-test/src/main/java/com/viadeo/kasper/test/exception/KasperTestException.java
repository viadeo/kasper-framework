// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.exception;

import com.viadeo.kasper.exception.KasperException;

/**
 *
 * Base Kasper commands runtime exception
 */
public class KasperTestException extends KasperException {
	private static final long serialVersionUID = -1102642646985641991L;

	public KasperTestException(final String message, final Exception e) {
		super(message, e);
	}

	public KasperTestException(final Exception e) {
		super(e);
	}

	public KasperTestException(final String message) {
		super(message);
	}	
	
}
