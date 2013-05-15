// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.exception;

/**
 *
 * Base Kasper domain repository exception
 */
public class KasperDomainRepositoryException extends KasperDomainException {
	private static final long serialVersionUID = -4143561919621864234L;

	// ------------------------------------------------------------------------
	
	public KasperDomainRepositoryException(final String message, final Exception e) {
		super(message, e);
	}

	public KasperDomainRepositoryException(final String message) {
		super(message);
	}
	
	public KasperDomainRepositoryException(final Exception e) {
		super(e);
	}
	
}
