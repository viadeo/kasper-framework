// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.EventSourcedRepository;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.test.platform.KasperAggregateFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;

@RunWith(Parameterized.class)
public class TestFixtureAggregateTest {

    private KasperAggregateFixture<TestAggregate> fixture;
    private IRepository<TestAggregate> testRepository;

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

    public TestFixtureAggregateTest(final IRepository testRepository) {
        this.testRepository = testRepository;
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

        // Given
        final KasperID createId = DefaultKasperId.random();

        // When command is supplied
        // Then we expect creation and first name changing events
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
    public void testSimpleUpdateAfterCreateCommand() {

        // Given
        final KasperID aggregateId = DefaultKasperId.random();

        // When command is supplied
        // Then we expect creation and first name changing events
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
        if ( ! EventSourcedRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            return;
        }

        // Given
        final KasperID aggregateId = DefaultKasperId.random();

        // When command is supplied
        // Then we expect creation and first name changing events
        fixture
                .given(
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
