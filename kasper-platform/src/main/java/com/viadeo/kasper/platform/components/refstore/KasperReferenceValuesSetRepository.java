// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.components.refstore;

import java.io.Serializable;
import java.util.Set;

import com.viadeo.kasper.ddd.values.IReferenceValue;

abstract public class KasperReferenceValuesSetRepository<V extends IReferenceValue<? extends Serializable>> {

	@SuppressWarnings("unused")
	private Set<? extends Serializable> values;
	
}
