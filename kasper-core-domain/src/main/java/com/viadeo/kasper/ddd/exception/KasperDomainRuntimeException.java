// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.exception;

import com.viadeo.kasper.exception.KasperRuntimeException;

/**
 *
 * Base Kasper runtime exception
 *
 */
public class KasperDomainRuntimeException extends KasperRuntimeException {
	private static final long serialVersionUID = 22465920385234496L;
	
	public KasperDomainRuntimeException(final String message, final Exception e) {
		super(message, e);
	}
	
	public KasperDomainRuntimeException(final String message) {
		super(message);
	}	

	public KasperDomainRuntimeException(final Exception e) {
		super(e);
	}
	
}
