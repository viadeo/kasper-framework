// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exception;

/** The base Kasper exception */
public class KasperException extends Exception {
	private static final long serialVersionUID = 8283132692513219950L;

	public KasperException() {
		super();
	}
	
	public KasperException(final String message, final Throwable e) {
		super(message, e);
	}

	public KasperException(final String message) {
		super(message);
	}

	public KasperException(final Throwable e) {
		super(e);
	}

}
