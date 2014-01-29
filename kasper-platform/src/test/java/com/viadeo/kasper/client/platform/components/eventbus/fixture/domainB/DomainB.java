// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.fixture.domainB;

import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.DomainA;
import com.viadeo.kasper.event.EventListener;

public class DomainB {

    public static class EventListenerA extends EventListener<DomainA.EventA> {
    }

    public static class EventListenerB extends EventListener<DomainA.EventA> {

    }
}
