package com.viadeo.kasper.eventhandling.cluster.fixture;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class Spy<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Spy.class);
    private static final long TIMEOUT = 8000L;
    public final CountDownLatch countDownLatch;
    public final List<T> actualEvents;

    public Spy(final Integer size) throws InterruptedException {
        this.countDownLatch = new CountDownLatch(size);
        this.actualEvents = Lists.newArrayList();
    }

    public void handle(T event) {

        if (0 == countDownLatch.getCount()) {
            throw new AssertionError("Unexpected event message : " + event);
        }
        actualEvents.add(event);
        LOGGER.debug("Received event message : {}", event);
        countDownLatch.countDown();
    }

    public int size() {
        return actualEvents.size();
    }

    public T get(int i) {
        return actualEvents.get(i);
    }

    public void await() {
        try {
            countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }
}

