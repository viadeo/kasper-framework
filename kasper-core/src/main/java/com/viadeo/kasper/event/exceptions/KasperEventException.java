// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.exceptions;

import com.viadeo.kasper.exception.KasperException;

/**
 *
 * Base Kasper commmands exception
 */
public class KasperEventException extends KasperException {

	private static final long serialVersionUID = 4333939739848393501L;

	public KasperEventException(final String message, final Exception e) {
		super(message, e);
	}	

	public KasperEventException(final String message) {
		super(message);
	}	
	
	public KasperEventException(final Exception e) {
		super(e);
	}	
	
}
