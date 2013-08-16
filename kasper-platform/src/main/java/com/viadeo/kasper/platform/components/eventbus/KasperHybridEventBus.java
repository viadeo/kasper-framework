// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.platform.components.eventbus;

import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.EventBusTerminal;

public class KasperHybridEventBus extends ClusteringEventBus  {

	public KasperHybridEventBus() {
		super();
	}
	
    public KasperHybridEventBus(final EventBusTerminal terminal) {
        super(terminal);
    }

}
