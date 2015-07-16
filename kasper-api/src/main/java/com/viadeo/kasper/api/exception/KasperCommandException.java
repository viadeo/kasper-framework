// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.exception;

/**
 *
 * Base Kasper commands runtime exception
 */
public class KasperCommandException extends KasperException {
	private static final long serialVersionUID = -1102642646985641991L;

	public KasperCommandException(final String message, final Exception e) {
		super(message, e);
	}	

	public KasperCommandException(final Exception e) {
		super(e);
	}	
	
	public KasperCommandException(final String message) {
		super(message);
	}	
	
}
