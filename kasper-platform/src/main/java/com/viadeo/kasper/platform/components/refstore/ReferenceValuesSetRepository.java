// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.components.refstore;

import com.viadeo.kasper.ddd.values.ReferenceValue;

import java.io.Serializable;
import java.util.Set;

public abstract class ReferenceValuesSetRepository<V extends ReferenceValue<? extends Serializable>> {

	protected Set<? extends Serializable> values;
	
}
