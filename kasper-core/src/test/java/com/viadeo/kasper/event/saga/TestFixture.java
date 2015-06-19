// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.annotation.XKasperSaga;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestFixture {

    @XKasperUnregistered
    @XKasperDomain(
            label = "TestDomain",
            prefix = "sec",
            description = "The Security domain",
            owner = "Emmanuel Camper <ecamper@viadeoteam.com>"
    )
    public class TestDomain implements Domain { }

    // --------------------------------------------------------------

    @XKasperSaga(domain = TestDomain.class)
    public static class TestSagaA implements Saga {

        public void init() {}

        public void init(Object object1, Object object2) {}

        public void init(Object object) {}

        public void handle0(TestEvent event) {}

        @XKasperSaga.Start(getter = "getId")
        public void handle(TestEvent event) {}

        @XKasperSaga.End(getter = "getId")
        public void handle2(TestEvent2 event) {}

        @XKasperSaga.Step(getter = "getId")
        public void handle3(TestEvent3 event) {}

        @XKasperSaga.Schedule(getter = "getId", delay = 1L, unit = TimeUnit.SECONDS)
        public void handle4(TestEvent4 event) {}

        @Override
        public Optional<SagaFactory> getFactory() {
            return Optional.absent();
        }
    }

    public static class TestEvent implements Event {
        private final String id;

        public TestEvent(String id) {
            this.id = id;
        }

        public String getId() { return id; }
        public String getIdThrowsException() { throw new RuntimeException("Fake exception"); }
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

    public static Method getMethod(Class<? extends Saga> sagaClass, String methodName, Class<? extends Event> eventClass) {
        try {
            return sagaClass.getMethod(methodName, eventClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // --------------------------------------------------------------

    @XKasperSaga(domain = TestDomain.class)
    public static class TestSagaB implements Saga {

        private final KasperCommandGateway commandGateway;
        private int count;
        private String name;

        public TestSagaB(KasperCommandGateway commandGateway) {
            this.commandGateway = commandGateway;
        }

        @XKasperSaga.Start(getter = "getId")
        public void start(StartEvent event){
            System.err.println("Saga is started !");
        }

        @XKasperSaga.Step(getter = "getId")
        public void step(StepEvent event){
            System.err.println("A step is invoked !");
            count++;
        }

        @XKasperSaga.Step(getter = "getId")
        public void throwException(ThrowExceptionEvent event){
            throw new RuntimeException("An exception is intended !");
        }

        @XKasperSaga.End(getter = "getId")
        public void end(EndEvent event){
            System.err.println("Saga is ended !");
        }

        @Override
        public Optional<SagaFactory> getFactory() {
            return Optional.absent();
        }

        public KasperCommandGateway getCommandGateway() {
            return commandGateway;
        }

        public int getCount() {
            return count;
        }

        public String getName() {
            return name;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class StartEvent extends AbstractEvent {
        public StartEvent(UUID id) {
            super(id);
        }
    }

    public static class StepEvent extends AbstractEvent {
        public StepEvent(UUID id) {
            super(id);
        }
    }

    public static class EndEvent extends AbstractEvent {
        public EndEvent(UUID id) {
            super(id);
        }
    }

    public static class ThrowExceptionEvent extends AbstractEvent {
        public ThrowExceptionEvent(UUID id) {
            super(id);
        }
    }

    private static class AbstractEvent implements Event {

        private final UUID id;

        public AbstractEvent(UUID id) {
            this.id = id;
        }

        public UUID getId(){
            return id;
        }
    }
}
