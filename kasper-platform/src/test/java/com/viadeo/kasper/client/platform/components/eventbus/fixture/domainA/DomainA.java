// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA;

import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;

public class DomainA {

    public static class EventA extends Event {
        private static final long serialVersionUID = 8982171461950633502L;
    }

    public static class EventListenerA extends EventListener<EventA> {
    }
}
