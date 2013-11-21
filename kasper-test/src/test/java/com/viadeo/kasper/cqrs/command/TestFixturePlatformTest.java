// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.axonframework.test.AxonAssertionError;
import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;
import static org.junit.Assert.fail;

public class TestFixturePlatformTest {

    private KasperPlatformFixture fixture;

    private static final String firstName = "Richard";
    private static final String lastName = "Stallman";

    // ========================================================================

    @Before
    public void resetFixture() {
        this.fixture = KasperPlatformFixture.scanPrefix(this.getClass().getPackage().getName());
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

    @Test
    public void testSimpleQueryOK() {
        fixture
            .given()
            .when(
                new TestQuery("OK")
            )
            .expectReturnResponse(
                new TestResult("42")
            );
    }

    @Test
    public void testSimpleQueryError() {
        fixture
            .given()
            .when(
                new TestQuery("ERROR")
            )
            .expectReturnError(
                new KasperReason("ERROR", "I'm bad")
            );
    }

    @Test
    public void testSimpleQueryRefused() {
        fixture
            .given()
            .when(
                new TestQuery("REFUSED")
            )
            .expectReturnRefused(
                new KasperReason("REFUSED", "Go To Hell")
            );
    }

}
