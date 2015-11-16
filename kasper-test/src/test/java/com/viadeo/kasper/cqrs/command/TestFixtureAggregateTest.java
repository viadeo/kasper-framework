// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.repository.AutowiredEventSourcedRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.test.platform.KasperAggregateFixture;
import org.axonframework.test.AxonAssertionError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestFixtureAggregateTest {

    private KasperAggregateFixture<TestAggregate> fixture;
    private Repository<KasperID,TestAggregate> testRepository;

    private static final String firstName = "Richard";
    private static final String lastName = "Stallman";


    // ========================================================================

    @Parameterized.Parameters
    public static Collection repositories() {
        return Arrays.asList(new Object[][] {
            { new TestRepository() }
            , { new TestEventRepository() }
        });
    }

    public TestFixtureAggregateTest(final Repository<KasperID,TestAggregate> testRepository) {
        this.testRepository = testRepository;
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    @Before
    public void resetFixture() {
        this.fixture = KasperAggregateFixture.forRepository(
            this.testRepository,
            TestAggregate.class
        );

        fixture.registerCommandHandler(new TestCreateCommandHandler());
        fixture.registerCommandHandler(new TestChangeLastNameCommandHandler());
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
                )
            )
            .expectReturnOK()
            .expectExactSequenceOfEvents(
                new TestCreatedEvent(createId),
                new TestFirstNameChangedEvent(firstName)
            );

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUnexpectedValidation() {

        final KasperID createId = DefaultKasperId.random();

        try {
            fixture
                .given()
                .when(
                    new TestCreateCommand(
                        createId,
                        null
                    )
                )
                .expectReturnOK();
            fail();
        } catch (final AxonAssertionError e) {
            // expected
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleExpectedValidation() {

        final KasperID createId = DefaultKasperId.random();

        fixture
            .given()
            .when(
                new TestCreateCommand(
                    createId,
                    null
                )
            )
            .expectValidationErrorOnField("firstName");
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleExpectedValidationOnBadField() {

        final KasperID createId = DefaultKasperId.random();

        try {
            fixture
                .given()
                .when(
                    new TestCreateCommand(
                        createId,
                        null
                    )
                )
                .expectValidationErrorOnField("foo");
        } catch (final AxonAssertionError e) {
            // expected
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateCommand() {

        final KasperID aggregateId = DefaultKasperId.random();

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
                )
            )
            .expectReturnOK()
            .expectExactSequenceOfEvents(
                new TestLastNameChangedEvent(lastName)
            );
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
            .givenEvents(
                new TestCreatedEvent(aggregateId),
                new TestFirstNameChangedEvent(firstName)
            )
            .when(
                new TestChangeLastNameCommand(
                    aggregateId,
                    lastName
                )
            )
            .expectReturnOK()
            .expectExactSequenceOfEvents(
                new TestLastNameChangedEvent(lastName)
            );
    }

}
