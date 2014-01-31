// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.fixture.domainB;


import com.viadeo.kasper.client.platform.components.eventbus.fixture.AbstractTestEventListener;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.DomainA;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class DomainB {

    public static class EventB extends Event {
        private static final long serialVersionUID = -4262868582314150260L;
    }

    public static class EventC extends Event {
        private static final long serialVersionUID = 3720447946899744730L;
    }

    public static class EventListenerA extends AbstractTestEventListener<DomainA.EventA> {

        public EventListenerA() {
            this(null, null);
        }

        public EventListenerA(final CountDownLatch countDownLatch, final Queue<DomainA.EventA> expectedEvents) {
            super(countDownLatch, expectedEvents);
        }
    }

    public static class EventListenerB extends EventListener<DomainB.EventB> {
    }
}
