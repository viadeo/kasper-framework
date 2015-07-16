// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.viadeo.kasper.api.id.AbstractKasperID;

public class KasperTestId extends AbstractKasperID<String> {
	private static final long serialVersionUID = 5764335949388759401L;

	public KasperTestId(final String id) {
		super(id);
	}
	
}
