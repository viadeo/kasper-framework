// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA;

import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.AbstractTestEventListener;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class DomainA {

    public static class EventA extends Event {
        private static final long serialVersionUID = 8982171461950633502L;
    }

    public static class EventC extends Event {
        private static final long serialVersionUID = 8982171461950633502L;
        private static int COUNTER = 0;

        private int id;

        public EventC() {
            this(COUNTER++);
        }

        public EventC(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class EventListenerA extends AbstractTestEventListener<EventA> {

        public EventListenerA() {
            this(null, null);
        }

        public EventListenerA(final CountDownLatch countDownLatch, final Queue<EventA> expectedEvents) {
            super(countDownLatch, expectedEvents);
        }
    }

    public static class EventListenerB extends AbstractTestEventListener<EventB> {
        public EventListenerB(final CountDownLatch countDownLatch, final Queue<EventB> expectedEvents) {
            super(countDownLatch, expectedEvents);
        }
    }

    public static class EventListenerC extends AbstractTestEventListener<EventC> {
        public EventListenerC(final CountDownLatch countDownLatch, final Queue<EventC> expectedEvents) {
            super(countDownLatch, expectedEvents);
        }
    }
}
