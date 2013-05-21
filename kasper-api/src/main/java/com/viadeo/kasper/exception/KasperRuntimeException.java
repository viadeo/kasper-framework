// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exception;

/**
 *
 * The base Kasper runtime exception
 *
 */
public class KasperRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4439295125026389937L;

	public KasperRuntimeException(final String message, final Throwable t) {
		super(message, t);
	}
	
	public KasperRuntimeException(final String message, final Exception e) {
		super(message, e);
	}

	public KasperRuntimeException(final String message) {
		super(message);
	}

	public KasperRuntimeException(final Exception e) {
		super(e);
	}

	public KasperRuntimeException(final Throwable e) {
		super(e);
	}
	
}
