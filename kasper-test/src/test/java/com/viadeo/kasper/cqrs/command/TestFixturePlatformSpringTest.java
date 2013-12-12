// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.domain.SpringDomainBundle;
import com.viadeo.kasper.impl.StringKasperId;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.GenericDomainEventMessage;
import org.axonframework.eventstore.EventStore;
import org.junit.Before;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFixturePlatformSpringTest extends TestFixturePlatformTest {

    @Before
    @Override
    public void resetFixture() {
        DomainEventMessage<FixtureUseCase.TestAggregate> domainEventMessage = new GenericDomainEventMessage<>("", 0L, new FixtureUseCase.TestAggregate(new StringKasperId("miaou")));

        DomainEventStream domainEventStream = mock(DomainEventStream.class);
        when(domainEventStream.peek()).thenReturn(domainEventMessage);

        EventStore eventStore = mock(EventStore.class);
        when(eventStore.readEvents(refEq("TestAggregate"), any())).thenReturn(domainEventStream);

        this.fixture = new KasperPlatformFixture();
        this.fixture.register(
                new SpringDomainBundle(
                          new FixtureUseCase.TestDomain()
                        , Lists.<Class>newArrayList(FixtureUseCaseSpringConfiguration.class)
                        , new SpringDomainBundle.BeanDescriptor(eventStore)
                )
        );
    }

}
