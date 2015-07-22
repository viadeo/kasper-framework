// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.exception;

import com.viadeo.kasper.api.exception.KasperQueryException;

public class KasperQueryAdapterException extends KasperQueryException {
	private static final long serialVersionUID = 6584426866366107126L;

	public KasperQueryAdapterException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public KasperQueryAdapterException(final String message) {
		super(message);
	}

	public KasperQueryAdapterException(final Throwable cause) {
		super(cause);
	}

}
