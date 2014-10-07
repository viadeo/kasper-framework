
Testing Kasper
========================


..  _Using_aggregate_fixture:

Using aggregate fixture
------------------------

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


..  _Using_platform_fixture:

Using platform fixture
------------------------

This fixture allows to place your test on a platform for a dedicated domain bundle. While the `AggregateFixture` define
a lower scope in order to test only one aggregate with its repository.
The `KasperPlatformFixture` boots a default kasper platform including your domain bundle and offer some possibility to test
your domain.

According to your domain context you have several ways to initialize the fixture, one of them :

.. code-block:: java
    :linenos:

    final DomainBundle domainBundle = new DefaultDomainBundle(new MySampleDomain());
    final KasperPlatformFixture fixture = new KasperPlatformFixture().register(domainBundle);

After have instantiated the fixture then yoou can define an execution context before test something thanks to 'given'
methods. When ready then you can :

**send a command then expect the result :**

.. code-block:: java
    :linenos:

    fixture.given()
            .when(new CreateUserCommand("110E8400-E29B-11D4-A716-446655440000", "Chuck", "Norris"))
            .expectReturnOk();

**request a query then expect the result :**

.. code-block:: java
    :linenos:

    fixture.given()
            .when(new GetUserQuery("110E8400-E29B-11D4-A716-446655440000"))
            .expectReturnOk()
            .expectReturnResponse(new UserQueryResult("Chuck", "Norris", "cnorris"));

**emit an event then expect the result :**

.. code-block:: java
    :linenos:

    fixture.given()
            .when(new UserCreatedEvent("110E8400-E29B-11D4-A716-446655440000"))
            .expectEventNotificationOn(UserCreatedEventListener.class, StatisticOnUserCreatedEvent.class;


..  _Using_platform_runner:

Using platform runner
------------------------

An another way to create an integration test is to use a custom runner using JUnit. This runner allows to mount a
platform in background and to access directly to its components via the `Inject` annotation.

We can inject:

* com.viadeo.kasper.client.platform.Platform
* com.viadeo.kasper.cqrs.command.CommandGateway
* com.viadeo.kasper.cqrs.query.QueryGateway
* com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus
* all domain bundles registered by the 'Bundles' annotation
* all bean defined by our infrastructure configuration referenced by the ''InfrastructureContext' annotation

In order to use it then annotate your class test  like this :

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.test.platform.PlatformRunner;
    import org.junit.runner.RunWith;

    @RunWith(PlatformRunner.class)
    public class SomethingITest...

In more of the runner, we have several annotations allowing to customize the platform that will be mounted.

**Bundles**:

This `Bundles` annotation specifies a set of bundle that will be added to the platform.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.test.platform.*;
    import org.junit.runner.RunWith;

    @RunWith(PlatformRunner.class)
    @Bundles({BundleA.class, BundleB.class})
    public class SomethingITest...

**Configuration**:

The `Configuration` annotation specifies which platform configuration will be used in order to build the platform.
By Default `KasperPlatformConfiguration` will be used.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.test.platform.*;
    import org.junit.runner.RunWith;

    @RunWith(PlatformRunner.class)
    @Configuration(MyCustomPlatformConfiguration.class)
    public class SomethingITest...

**Infrastructure context**:

The `InfrastructureContext` annotation is used to determine how to load and configure an ApplicationContext containing
infrastructure components that can be required in order to instantiate bundle.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.test.platform.*;
    import org.junit.runner.RunWith;

    @RunWith(PlatformRunner.class)
    @InfrastructureContext(
        configurations = {InfrastructureConfiguration.class},
        activeProfiles = {"embedded"}
    )
    public class SomethingITest...