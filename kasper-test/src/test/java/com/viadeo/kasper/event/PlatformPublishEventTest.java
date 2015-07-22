// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.collect.Lists;
import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.KasperTestIdGenerator;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.core.component.event.saga.Saga;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class PlatformPublishEventTest extends AbstractPlatformTests {

    private static boolean received = false;

    @Test
    public void testPublishEvent() throws InterruptedException, Exception {
        // Given
        final KasperID id = KasperTestIdGenerator.get();
        final Event event = new TestEvent(id);

        // When
        this.getPlatform().getEventBus().publishEvent(newContext(), event);
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
    public static class TestListener extends EventListener<TestEvent> {
        @Override
        public EventResponse handle(Context context, TestEvent event) {
            received = true;
            return EventResponse.success();
        }
    }

}
