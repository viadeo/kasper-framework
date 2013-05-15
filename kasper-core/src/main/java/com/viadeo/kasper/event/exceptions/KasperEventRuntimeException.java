// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.exceptions;

import com.viadeo.kasper.exception.KasperRuntimeException;

/**
 *
 * Base Kasper commands runtime exception
 */
public class KasperEventRuntimeException extends KasperRuntimeException {

	private static final long serialVersionUID = -1102688646985641991L;

	public KasperEventRuntimeException(String message, Exception e) {
		super(message, e);
	}	

	public KasperEventRuntimeException(Exception e) {
		super(e);
	}	
	
	public KasperEventRuntimeException(String message) {
		super(message);
	}	
	
}
