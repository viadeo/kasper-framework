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
package com.viadeo.kasper.event;

import com.google.common.collect.Lists;
import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class PlatformPublishEventTest extends AbstractPlatformTests {

    private static boolean received = false;

    @Test
    public void testPublishEvent() throws Exception {
        // Given
        final KasperID id = KasperTestIdGenerator.get();
        final Event event = new TestEvent(id);

        // When
        this.getPlatform().getEventBus().publish(newContext(), event);
        Thread.sleep(3000);

        // Then
        assertTrue(received);
    }

    @Override
    public List<DomainBundle> getBundles() {
        return Lists.<DomainBundle>newArrayList(
                new DefaultDomainBundle(
                        Lists.<CommandHandler>newArrayList(),
                        Lists.<QueryHandler>newArrayList(),
                        Lists.<Repository>newArrayList(),
                        Lists.<EventListener>newArrayList(new TestListener()),
                        Lists.<Saga>newArrayList(),
                        Lists.<QueryInterceptorFactory>newArrayList(),
                        Lists.<CommandInterceptorFactory>newArrayList(),
                        Lists.<EventInterceptorFactory>newArrayList(),
                        new TestDomain(),
                        "testDomain"
                )
        );
    }

    // ------------------------------------------------------------------------

    @XKasperDomain(label = "testDomain" , prefix = "tst" , description = "test domain")
    public static class TestDomain implements Domain {
    }

    @XKasperConcept(label = "test root concept" , domain = TestDomain.class)
    public static class TestRootConcept extends Concept {
    }

    @SuppressWarnings("serial")
    @XKasperEvent(action = "test")
    public static class TestEvent extends EntityCreatedEvent<TestDomain> {
        public TestEvent(final KasperID idShortMessage) {
            super(idShortMessage);
        }
    }

    @XKasperEventListener(domain = TestDomain.class)
    public static class TestListener extends AutowiredEventListener<TestEvent> {
        @Override
        public EventResponse handle(Context context, TestEvent event) {
            received = true;
            return EventResponse.success();
        }
    }

}
