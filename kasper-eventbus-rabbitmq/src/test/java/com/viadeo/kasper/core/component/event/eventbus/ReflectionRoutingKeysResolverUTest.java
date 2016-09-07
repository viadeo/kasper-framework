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

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.MultiEventListener;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReflectionRoutingKeysResolverUTest {

    private ReflectionRoutingKeysResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new ReflectionRoutingKeysResolver();
    }

    @Test
    public void resolve_withEventListener_usingAbstractEvent_isOk() {
        // When
        Set<RoutingKeys.RoutingKey> keys = resolver.resolve(new AbstractEventListener()).get();

        // Then
        assertNotNull(keys);
        assertEquals(3, keys.size());
        assertEquals(
                Sets.newHashSet(
                        new RoutingKeys.RoutingKey(MyAbstractEvent.class.getName()),
                        new RoutingKeys.RoutingKey(MySpeOneAbstractEvent.class.getName()),
                        new RoutingKeys.RoutingKey(MySpeTwoAbstractEvent.class.getName())
                ),
                Sets.newHashSet(keys)
        );
    }

    @Test
    public void resolve_withEventListener_usingConcreteEvent_isOk() {
        // When
        Set<RoutingKeys.RoutingKey> keys = resolver.resolve(new ConcreteEventListener()).get();

        // Then
        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertEquals(new RoutingKeys.RoutingKey(MyConcreteEvent.class.getName()), keys.iterator().next());
    }

    @Test
    public void resolve_withEventListener_usingInterface_isOk() {
        // When
        Set<RoutingKeys.RoutingKey> keys = resolver.resolve(new InterfaceEventListener()).get();

        // Then
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertEquals(
                Sets.newHashSet(
                        new RoutingKeys.RoutingKey(MyInterfaceEvent.class.getName()),
                        new RoutingKeys.RoutingKey(MySpeAbstractEvent.class.getName())
                ),
                Sets.newHashSet(keys)
        );
    }

    @Test
    public void resolve_withEventListener_usingIEvent_isOk() {
        // When
        Set<RoutingKeys.RoutingKey> keys = resolver.resolve(new IEventListener()).get();

        // Then
        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertEquals(new RoutingKeys.RoutingKey("#"), keys.iterator().next());
    }

    @Test
    public void resolve_withDeprecatedEventListener_usingIEvent_isOk() {
        // When
        RoutingKeys routingKeys = resolver.resolve(new MultiEventListenerTest());

        // Then
        Set<RoutingKeys.RoutingKey> keys = routingKeys.get();
        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertEquals(new RoutingKeys.RoutingKey(MyConcreteEvent.class.getName()), keys.iterator().next());

        Set<RoutingKeys.RoutingKey> deprecatedKeys = routingKeys.deprecated();
        assertNotNull(deprecatedKeys);
        assertEquals(
                Sets.newHashSet(
                        new RoutingKeys.RoutingKey(MyInterfaceEvent.class.getName(), Boolean.TRUE),
                        new RoutingKeys.RoutingKey(MySpeAbstractEvent.class.getName(), Boolean.TRUE)
                ),
                Sets.newHashSet(deprecatedKeys)
        );
    }

    private static class MyConcreteEvent implements Event {}
    private interface MyInterfaceEvent extends Event {}
    private interface MySpeAbstractEvent extends MyInterfaceEvent {}
    private static class MyAbstractEvent implements Event {}
    private static class MySpeOneAbstractEvent extends MyAbstractEvent {}
    private static class MySpeTwoAbstractEvent extends MyAbstractEvent {}

    private static class IEventListener extends AutowiredEventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }
    private static class ConcreteEventListener extends AutowiredEventListener<MyConcreteEvent> {
        @Override
        public EventResponse handle(Context context, MyConcreteEvent event) {
            return EventResponse.success();
        }
    }
    private static class AbstractEventListener extends AutowiredEventListener<MyAbstractEvent> {
        @Override
        public EventResponse handle(Context context, MyAbstractEvent event) {
            return EventResponse.success();
        }
    }
    private static class InterfaceEventListener extends AutowiredEventListener<MyInterfaceEvent> {
        @Override
        public EventResponse handle(Context context, MyInterfaceEvent event) {
            return EventResponse.success();
        }
    }
    private static class MultiEventListenerTest extends MultiEventListener {
        @Handle
        public EventResponse handle(MyConcreteEvent event) {
            return EventResponse.success();
        }
        @Deprecated
        @Handle
        public EventResponse handle(MyInterfaceEvent event) {
            return EventResponse.success();
        }
    }
}
