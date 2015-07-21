package com.viadeo.kasper.core.component.eventbus;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public interface KasperEventBusFixture {

    public static class ChildEvent extends UserEvent {

        public ChildEvent(String firstName, String lastName, Integer age) {
            super(firstName, lastName, age);
        }
    }

    public static class ChildEventListener extends EventListener<UserEvent> {
        private Spy<UserEvent> spy;

        public ChildEventListener(final Spy<UserEvent> spy) {
            this.spy = spy;
        }

        @Override
        public EventResponse handle(Context context, UserEvent userEvent) {
            spy.handle(userEvent);
            return EventResponse.success();
        }
    }

    public static class Spy<T> {
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

    public static class UserEvent implements Event {
        public String firstName;
        public String lastName;
        public Integer age;

        public UserEvent(String firstName, String lastName, Integer age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(firstName, lastName, age);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final UserEvent other = (UserEvent) obj;
            return Objects.equal(this.firstName, other.firstName) && Objects.equal(this.lastName, other.lastName) && Objects.equal(this.age, other.age);
        }
    }

    public static class UserEventListener extends EventListener<UserEvent> {

        private Spy<UserEvent> spy;


        public UserEventListener(final Spy<UserEvent> spy) throws InterruptedException {
            this.spy = spy;
        }

        @Override
        public EventResponse handle(Context context, UserEvent userEvent) {
            spy.handle(userEvent);
            return EventResponse.success();
        }
    }
}
