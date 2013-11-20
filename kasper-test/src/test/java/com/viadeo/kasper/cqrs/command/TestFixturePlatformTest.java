// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;

public class TestFixturePlatformTest {

    private KasperPlatformFixture fixture;

    private static final String firstName = "Richard";
    private static final String lastName = "Stallman";

    // ========================================================================

    @Before
    public void resetFixture() {
        this.fixture = KasperPlatformFixture.forPrefix(this.getClass().getPackage().getName());
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

}
