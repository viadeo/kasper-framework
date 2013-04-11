// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.exceptions;

import com.viadeo.kasper.exception.KasperRuntimeException;

public class KasperPlatformRuntimeException extends KasperRuntimeException {

	private static final long serialVersionUID = -5911149302021595640L;

	public KasperPlatformRuntimeException(String message, Exception e) {
		super(message, e);
	}	

	public KasperPlatformRuntimeException(String message) {
		super(message);
	}	
	
	public KasperPlatformRuntimeException(Exception e) {
		super(e);
	}	
	
}
