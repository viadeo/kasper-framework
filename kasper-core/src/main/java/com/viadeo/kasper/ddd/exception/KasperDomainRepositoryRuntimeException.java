// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.exception;

/**
 *
 * Base Kasper domain repository runtime exception
 *
 */
public class KasperDomainRepositoryRuntimeException extends KasperDomainRuntimeException {
	private static final long serialVersionUID = -4143544919621864234L;

	public KasperDomainRepositoryRuntimeException(final String message, final Exception e) {
		super(message, e);
	}
	
	public KasperDomainRepositoryRuntimeException(final String message) {
		super(message);
	}	

	public KasperDomainRepositoryRuntimeException(final Exception e) {
		super(e);
	}
	
}
