// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.exceptions;

import com.viadeo.kasper.exception.KasperException;

public class KasperPlatformException extends KasperException {

	private static final long serialVersionUID = -5942149302021595640L;

	public KasperPlatformException(String message, Exception e) {
		super(message, e);
	}	

	public KasperPlatformException(String message) {
		super(message);
	}	
	
	public KasperPlatformException(Exception e) {
		super(e);
	}	
	
}
