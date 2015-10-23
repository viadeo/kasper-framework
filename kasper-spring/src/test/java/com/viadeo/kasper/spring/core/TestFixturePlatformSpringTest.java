// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.viadeo.kasper.cqrs.command.FixtureUseCase;
import com.viadeo.kasper.cqrs.command.FixtureUseCaseSpringConfiguration;
import com.viadeo.kasper.spring.platform.SpringDomainBundle;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.SimpleDomainEventStream;
import org.axonframework.eventstore.EventStore;
import org.junit.Before;

import java.util.Collection;
import java.util.List;

public class TestFixturePlatformSpringTest {

    protected KasperPlatformFixture fixture;

    // ========================================================================

    @Before
    public void resetFixture() {
        EventStore eventStore = new EventStore() {
            private final Multimap<String, DomainEventMessage> messageByIdentifiers = Multimaps.newListMultimap(
                    Maps.<String, Collection<DomainEventMessage>>newHashMap(),
                    new Supplier<List<DomainEventMessage>>() {
                        @Override
                        public List<DomainEventMessage> get() {
                            return Lists.newArrayList();
                        }
                    }
            );

            @Override
            public void appendEvents(String type, DomainEventStream events) {
                while (events.hasNext()) {
                    final DomainEventMessage eventMessage = events.next();
                    messageByIdentifiers.put(String.valueOf(eventMessage.getAggregateIdentifier()), eventMessage);
                }
            }

            @Override
            public DomainEventStream readEvents(String type, Object identifier) {
                return new SimpleDomainEventStream(messageByIdentifiers.get(String.valueOf(identifier)));
            }
        };

        final SpringDomainBundle domainBundle = new SpringDomainBundle(
                new FixtureUseCase.TestDomain()
                , Lists.<Class>newArrayList(FixtureUseCaseSpringConfiguration.class)
                , new SpringDomainBundle.BeanDescriptor(eventStore)
        );

        this.fixture = new KasperPlatformFixture();
        this.fixture.register(domainBundle);
    }

}
