package com.viadeo.kasper;

import org.axonframework.domain.IdentifierFactory;

import com.viadeo.kasper.IKasperID;

public class KasperTestIdGenerator {

	@SuppressWarnings("unchecked") // Delagated to client
	static public <I extends IKasperID> I get() {
		final String uuid = IdentifierFactory.getInstance().generateIdentifier();
		return (I) new KasperTestId(uuid);
	}

	@SuppressWarnings("unchecked") // Delegated to client
	static public <I extends IKasperID> I get(final String id) {
		return (I) new KasperTestId(id);
	}

}
