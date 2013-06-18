// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.exceptions;

/**
 *
 * Base Kasper commands runtime exception
 */
public class KasperCommandException extends RuntimeException {
	private static final long serialVersionUID = -1102642646985641991L;

	public KasperCommandException(String message, Exception e) {
		super(message, e);
	}	

	public KasperCommandException(Exception e) {
		super(e);
	}	
	
	public KasperCommandException(String message) {
		super(message);
	}	
	
}
