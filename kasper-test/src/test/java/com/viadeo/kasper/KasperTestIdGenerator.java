// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import org.axonframework.domain.IdentifierFactory;

public final class KasperTestIdGenerator {

	private KasperTestIdGenerator() { /* singleton */ }
	
	@SuppressWarnings("unchecked") // Delegated to client
	public static <I extends KasperID> I get() {
		final String uuid = IdentifierFactory.getInstance().generateIdentifier();
		return (I) new KasperTestId(uuid);
	}

	@SuppressWarnings("unchecked") // Delegated to client
	public static <I extends KasperID> I get(final String id) {
		return (I) new KasperTestId(id);
	}

}
