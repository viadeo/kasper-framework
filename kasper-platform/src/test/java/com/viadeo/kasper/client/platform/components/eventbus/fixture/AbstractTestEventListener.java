// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.fixture;

import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class AbstractTestEventListener<EVENT extends Event> extends EventListener<EVENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestEventListener.class);

    public final CountDownLatch countDownLatch;
    public final Queue<EVENT> expectedEvents;

    public AbstractTestEventListener(final CountDownLatch countDownLatch, final Queue<EVENT> expectedEvents) {
        this.countDownLatch = countDownLatch;
        this.expectedEvents = expectedEvents;
    }

    @Override
    public void handle(final EVENT event) {
        LOGGER.info("handling event: {}" + event);

        if(null != countDownLatch){
            if(0 == countDownLatch.getCount()){
                throw new AssertionError("unexpected event : " + event);
            }
            countDownLatch.countDown();
            Assert.assertEquals(expectedEvents.poll(), event);
        }
    }
}