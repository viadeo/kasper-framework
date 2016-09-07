// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.SchedulableSagaMethod;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import org.joda.time.DateTime;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestFixture {

    @XKasperUnregistered
    @XKasperDomain(
            label = "Test",
            prefix = "sec",
            description = "a domain",
            owner = "Emmanuel Camper <ecamper@viadeoteam.com>"
    )
    public class TestDomain implements Domain { }

    // --------------------------------------------------------------

    @XKasperSaga(domain = TestDomain.class)
    public static class TestSagaA implements Saga {

        public void init() {}

        public void init(Object object1, Object object2, Object object3) {}

        public void init(Object object1, Object object2) {}

        public void init(Object object) {}

        public void handle0(TestEvent event) {}

        @XKasperSaga.Start(getter = "getId")
        public void handle(TestEvent event) {}

        @XKasperSaga.End(getter = "getId")
        public void handle2(TestEvent2 event) {}

        @XKasperSaga.Step(getter = "getId")
        public void handle3(TestEvent3 event) {}

        @XKasperSaga.Schedule(delay = 1L, unit = TimeUnit.SECONDS, methodName = "test")
        public void handle4(TestEvent4 event) {}

        public void handle5(Context context, TestEvent3 event) {}

        public void handle5(TestEvent3 event, Context context) {}

        public void handle6(EventMessage<TestEvent3> eventMessage) {}

        public void handle7(EventMessage eventMessage) {}

        public void handle8(EventMessage<?> eventMessage) {}

        @XKasperSaga.Step(getter = "getId")
        public void notScheduledStep(TestEvent5 event) {}

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.Schedule(delay = 1L, unit = TimeUnit.SECONDS, methodName = "test")
        public void scheduledStep(TestEvent6 event) {}

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.Schedule(delay = 1L, unit = TimeUnit.SECONDS, methodName = "test")
        @XKasperSaga.CancelSchedule(methodName = "test")
        public void scheduledAndCancelStep(TestEvent6 event) {}

        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
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

    public static class TestEvent5 extends TestEvent {
        public TestEvent5(String id) {
            super(id);
        }
    }

    public static class TestEvent6 extends TestEvent {
        public TestEvent6(String id) {
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
        private int invokedMethodCount;
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

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.Schedule(delay = 100, unit = TimeUnit.MILLISECONDS, methodName = "invokedMethod")
        public void scheduledStep(StepEvent1 event){
            System.err.println("A method invocation is scheduled !");
        }

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.ScheduledByEvent(methodName = "invokedMethod")
        public void scheduledByEventStep(StepEvent4 event){ }

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.CancelSchedule(methodName = "invokedMethod")
        public void cancelScheduledStep(StepEvent2 event){
            System.err.println("A scheduled method invocation is canceled !");
        }

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.Schedule(methodName = "invokedMethod", end = true, unit = TimeUnit.MILLISECONDS, delay = 150)
        public void scheduledAndEnd(StepEvent5 event){ }

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.Schedule(delay = 1, unit = TimeUnit.SECONDS, methodName = "invokedMethod")
        public void sleepOneSec(StepEvent3 event){
            System.err.println("A method invocation is scheduled !");
        }

        @XKasperSaga.End(getter = "getId")
        public void end(EndEvent event){
            System.err.println("Saga is ended !");
        }

        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
            return Optional.absent();
        }

        public void invokedMethod() {
            System.err.println("the method is invoked !");
            invokedMethodCount++;
        }

        public KasperCommandGateway getCommandGateway() {
            return commandGateway;
        }

        public int getCount() {
            return count;
        }

        public int getInvokedMethodCount() {
            return invokedMethodCount;
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

    // --------------------------------------------------------------

    @XKasperSaga(domain = TestDomain.class)
    public static class TestSagaC implements Saga {

        @XKasperSaga.Start(getter = "getId")
        public void start(StartEvent event){
            System.err.println("Saga is started !");
        }

        @XKasperSaga.Step(getter = "getId")
        @XKasperSaga.ScheduledByEvent(methodName = "invokedMethod")
        public void scheduledByEventStep(StepEvent1 event){ }

        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
            return Optional.absent();
        }

        @XKasperSaga.End(getter = "getId")
        @XKasperSaga.CancelSchedule(methodName = "invokedMethod")
        public void endWithCancelSchedule(StepEvent3 event){
            System.err.println("Saga is ended !");
        }

        public void invokedMethod(){
            System.err.println("the method is invoked !");
        }
    }

    // --------------------------------------------------------------

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

    public static class StepEvent1 extends AbstractEvent {
        public StepEvent1(UUID id) {
            super(id);
        }
    }

    public static class StepEvent2 extends AbstractEvent {
        public StepEvent2(UUID id) {
            super(id);
        }
    }

    public static class StepEvent3 extends AbstractEvent {
        public StepEvent3(UUID id) {
            super(id);
        }
    }

    public static class StepEvent4 extends AbstractEvent implements SchedulableSagaMethod {

        private final DateTime scheduledTime;

        public StepEvent4(UUID id, DateTime scheduledTime) {
            super(id);
            this.scheduledTime = scheduledTime;
        }

        @Override
        public DateTime getScheduledDate() {
            return scheduledTime;
        }
    }

    public static class StepEvent5 extends AbstractEvent {
        public StepEvent5(UUID id) {
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

    public static class AbstractEvent implements Event {

        private final UUID id;

        public AbstractEvent(UUID id) {
            this.id = id;
        }

        public UUID getId(){
            return id;
        }
    }
}
