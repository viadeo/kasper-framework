Testing Kasper framework
========================

Aggregate test fixture
----------------------

The aggregate test fixture is essentially based on Axon framework test fixtures : it's an integration test
with all platform components mocked.

An aggregate test fixture is **concerned about testing one aggregate and its repository** but
can interact with multiple commands and associated handlers.

You have to add manually your own components - command handlers and event listeners - to the fixture
which then offers you a simple fluent API and make some standard tests for you like equality between
stored events and published events and correct event-sourcing writing of your aggregates.

.. code-block:: java
    :linenos:

    final KasperID createId = DefaultKasperId.random();

    KasperAggregateFixture
        .forRepository(this.testRepository, TestAggregate.class)
        .registerCommandHandler(new TestCreateCommandHandler());
        .registerCommandHandler(new TestChangeLastNameCommandHandler());
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

Platform test fixture
---------------------

Unlike the Aggregate test fixture which uses mocked components and is only concerned about one aggregate
 and its repository, the Platform test fixture boots a default Kasper platform and will then use
 all internal mechanisms of the Kasper framework.

The idea is to test a given domain or part of a domain.

You instantiate it giving a java package name to scan and you're done :

.. code-block:: java
    :linenos:

    final KasperID aggregateId = DefaultKasperId.random();

    KasperPlatformFixture
        .scanPrefix("com.viadeo.platform.subscription");
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


        fixture
            .given()
            .when(
                new TestQuery("OK")
            )
            .expectReturnResponse(
                new TestResult("42")
            );


        fixture
            .given()
            .when(
                new TestQuery("ERROR")
            )
            .expectReturnError(
                new KasperReason("ERROR", "I'm bad")
            );


        fixture
            .given()
            .when(
                new TestQuery("REFUSED")
            )
            .expectReturnRefused(
                new KasperReason("REFUSED", "Go To Hell")
            );

