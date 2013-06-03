package com.viadeo.kasper;

import org.axonframework.domain.IdentifierFactory;

public final class KasperTestIdGenerator {

	private KasperTestIdGenerator() { /* singleton */ }
	
	@SuppressWarnings("unchecked") // Delegated to client
	public static <I extends IKasperID> I get() {
		final String uuid = IdentifierFactory.getInstance().generateIdentifier();
		return (I) new KasperTestId(uuid);
	}

	@SuppressWarnings("unchecked") // Delegated to client
	public static <I extends IKasperID> I get(final String id) {
		return (I) new KasperTestId(id);
	}

}
