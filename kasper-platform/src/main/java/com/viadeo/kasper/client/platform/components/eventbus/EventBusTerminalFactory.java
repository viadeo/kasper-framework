// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.eventhandling.EventBusTerminal;

public interface EventBusTerminalFactory {
    EventBusTerminal createEventBusTerminal();
}
