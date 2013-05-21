// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.exceptions;

import com.viadeo.kasper.exception.KasperRuntimeException;

/**
 *
 * Base Kasper commands runtime exception
 */
public class KasperCommandRuntimeException extends KasperRuntimeException {
	private static final long serialVersionUID = -1102642646985641991L;

	public KasperCommandRuntimeException(String message, Exception e) {
		super(message, e);
	}	

	public KasperCommandRuntimeException(Exception e) {
		super(e);
	}	
	
	public KasperCommandRuntimeException(String message) {
		super(message);
	}	
	
}
