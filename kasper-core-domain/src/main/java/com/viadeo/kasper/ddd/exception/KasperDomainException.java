// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.exception;

import com.viadeo.kasper.exception.KasperException;

/**
 *
 * Base Kasper domain exception
 */
public class KasperDomainException extends KasperException {
	private static final long serialVersionUID = 22665920385239496L;
	
	// ------------------------------------------------------------------------
	
	public KasperDomainException(final String message, final Exception e) {
		super(message, e);
	}

	public KasperDomainException(final String message) {
		super(message);
	}	
	
	public KasperDomainException(final Exception e) {
		super(e);
	}
	
}
