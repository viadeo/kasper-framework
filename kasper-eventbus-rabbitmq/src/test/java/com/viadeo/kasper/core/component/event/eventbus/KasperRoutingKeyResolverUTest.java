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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KasperRoutingKeyResolverUTest {


    private ReflectionRoutingKeysResolver routingKeyResolver;

    static public class EventB implements Event {
    }

    static public class EventA extends EventB {
    }

    static public class EventListenerB extends AutowiredEventListener<EventB> {
        @Override
        public EventResponse handle(Context context, EventB event) {
            return EventResponse.success();
        }
    }

    static public class EventListenerA extends AutowiredEventListener {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    static public class CatchAllEventListener extends AutowiredEventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    @Before
    public void setUp() throws Exception {

        routingKeyResolver = new ReflectionRoutingKeysResolver();

    }

    @Test
    public void subscribe_WithCatchAllHandler_CreatesOneBinding() throws Exception {

        // When
        RoutingKeys routingKeys = routingKeyResolver.resolve(new CatchAllEventListener());

        assertEquals(Sets.newHashSet(new RoutingKeys.RoutingKey("#")), routingKeys.get());
    }

    @Test
    public void subscribe_WithParentListener_CreatesOneBindingForEachClassInInheritenceTree() throws Exception {

        // When
        RoutingKeys routingKeys = routingKeyResolver.resolve(new EventListenerB());

        assertEquals(Sets.<RoutingKeys.RoutingKey>newHashSet(
                new RoutingKeys.RoutingKey("com.viadeo.kasper.core.component.event.eventbus.KasperRoutingKeyResolverUTest$EventB"),
                new RoutingKeys.RoutingKey("com.viadeo.kasper.core.component.event.eventbus.KasperRoutingKeyResolverUTest$EventA")
        ), routingKeys.get());
    }
}
