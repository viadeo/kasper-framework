// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.gateway.AxonCommandHandler;
import com.viadeo.kasper.core.component.command.gateway.ContextualizedUnitOfWork;
import com.viadeo.kasper.core.component.command.repository.*;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;
import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;
import static org.axonframework.test.matchers.Matchers.*;

@RunWith(Parameterized.class)
public class TestFixtureAxonTest {

    private FixtureConfiguration<TestAggregate> fixture;
    private Repository<KasperID,TestAggregate> testRepository;

    private static final String firstName = "Richard";
    private static final String lastName = "Stallman";

    // ========================================================================

    @Parameterized.Parameters
    public static Collection repositories() {
        return Arrays.asList(new Object[][] {
            { new TestRepository() },
            { new TestEventRepository() }
        });
    }

    public TestFixtureAxonTest(final Repository<KasperID,TestAggregate> testRepository) {
        if (null != testRepository) {
            this.testRepository = testRepository;
        }
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    @Before
    public void resetFixture() {
        this.fixture = Fixtures.newGivenWhenThenFixture(TestAggregate.class);
        fixture.setReportIllegalStateChange(true);
        fixture.registerIgnoredField(AggregateRoot.class, "version");
        ((SimpleCommandBus)this.fixture.getCommandBus()).setUnitOfWorkFactory(new ContextualizedUnitOfWork.Factory());

        if (WirableRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            ((WirableRepository) this.testRepository).setEventBus(fixture.getEventBus());
        }

        if (WirableEventSourcedRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            ((WirableEventSourcedRepository) this.testRepository).setEventStore(fixture.getEventStore());
        }

        // special case for which we attach an event store in order to apply our assertions
        if (AutowiredRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            ((AutowiredRepository)this.testRepository).setEventStore(fixture.getEventStore());
        }

        // Register the update handler
        final TestChangeLastNameCommandHandler updateHandler = new TestChangeLastNameCommandHandler();
        updateHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestChangeLastNameCommand.class, new AxonCommandHandler<>(updateHandler));

        // Register the create handler
        final TestCreateCommandHandler createHandler = new TestCreateCommandHandler();
        createHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestCreateCommand.class, new AxonCommandHandler<>(createHandler));
    }

    private Map<String, Object> newContext() {
        return new HashMap<String, Object>() {{
            this.put(Context.METANAME, Contexts.empty());
        }};
    }

    // ========================================================================

    @Test
    public void testSimpleCreation() {

        final KasperID createId = DefaultKasperId.random();

        fixture
            .given()
            .when(
                    new TestCreateCommand(
                            createId,
                            firstName
                    ),
                    newContext()
            )
            .expectReturnValue(CommandResponse.ok())
            .expectEvents(new TestCreatedEvent(createId), new TestFirstNameChangedEvent(firstName))
            .expectEventsMatching(payloadsMatching(exactSequenceOf(
                    equalTo(new TestCreatedEvent(createId)),
                    equalTo(new TestFirstNameChangedEvent(firstName)),
                    andNoMore()
            )));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateCommand() {

        // Given
        final KasperID aggregateId = DefaultKasperId.random();

        // When command is made, Then we expect creation and first name changing events
        fixture
                .givenCommands(
                        new TestCreateCommand(
                                aggregateId,
                                firstName
                        )
                )
                .when(
                        new TestChangeLastNameCommand(
                                aggregateId,
                                lastName
                        ),
                        newContext()
                )
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                    equalTo(new TestLastNameChangedEvent(lastName)),
                    andNoMore()
            )));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateEvents() {

        /**
         * Non-event sourced repositories cannot handle "given" events
         */
        if ( ! AutowiredEventSourcedRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            return;
        }

        final KasperID aggregateId = DefaultKasperId.random();

        fixture
            .given(
                new TestCreatedEvent(aggregateId),
                new TestFirstNameChangedEvent(firstName)
            )
            .when(
                new TestChangeLastNameCommand(
                    aggregateId,
                    lastName
                ),
                newContext()
            )
            .expectReturnValue(CommandResponse.ok())
            .expectEventsMatching(payloadsMatching(exactSequenceOf(
                equalTo(new TestLastNameChangedEvent(lastName)),
                andNoMore()
            )));
    }

}
