// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

public enum EventBusPolicy {

    /**
     * This policy allows to publish on the bus and consume from it
     */
    NORMAL,

    /**
     * This policy allows only to publish events on the bus. No event consumption will be done.
     */
    ONLY_PUBLISH
}
