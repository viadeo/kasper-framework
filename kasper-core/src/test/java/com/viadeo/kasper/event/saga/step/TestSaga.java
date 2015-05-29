package com.viadeo.kasper.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class TestSaga implements Saga {

    public void init() {}

    public void init(Object object1, Object object2) {}

    public void init(Object object) {}

    public void handle0(TestEvent event) {}

    @XKasperSaga.Start(getter = "getId")
    public void handle(TestEvent event) {}

    @XKasperSaga.End(getter = "getId")
    public void handle2(TestEvent2 event) {}

    @XKasperSaga.BasicStep(getter = "getId")
    public void handle3(TestEvent3 event) {}

    @XKasperSaga.Schedule(getter = "getId", delay = 1L, unit = TimeUnit.SECONDS)
    public void handle4(TestEvent4 event) {}

    @Override
    public Optional<SagaFactory> getFactory() {
        return Optional.absent();
    }

    public static class TestEvent implements Event {
        private final String id;

        public TestEvent(String id) {
            this.id = id;
        }

        public String getId() { return id; }
    }

    public static class TestEvent2 extends TestEvent {
        public TestEvent2(String id) {
            super(id);
        }
    }

    public static class TestEvent3 extends TestEvent {
        public TestEvent3(String id) {
            super(id);
        }
    }

    public static class TestEvent4 extends TestEvent {
        public TestEvent4(String id) {
            super(id);
        }
    }

    public static Method getMethod(String methodName, Class<? extends Event> eventClass) {
        try {
            return TestSaga.class.getMethod(methodName, eventClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
