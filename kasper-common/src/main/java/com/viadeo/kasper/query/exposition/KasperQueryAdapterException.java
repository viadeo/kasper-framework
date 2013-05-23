package com.viadeo.kasper.query.exposition;

public class KasperQueryAdapterException extends RuntimeException {
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
