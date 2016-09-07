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
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public interface KasperEventBusFixture {

    class ChildEvent extends UserEvent {

        public ChildEvent(String firstName, String lastName, Integer age) {
            super(firstName, lastName, age);
        }
    }

    class ChildEventListener extends AutowiredEventListener<UserEvent> {
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

    class Spy<T> {
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

    class UserEvent implements Event {
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

    class UserEventListener extends AutowiredEventListener<UserEvent> {

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
