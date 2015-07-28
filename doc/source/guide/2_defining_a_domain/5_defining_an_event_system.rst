
Defining an event system
========================

Events are the nervous system of the Kasper platform.

Their counter-part are the event listeners, which can be defined either in the **COMMAND** or **QUERY**
architectural areas.

**Command listeners** receive events and send commands or interact with some domain services.

**Query listeners** denormalize events within query indexes.


..  _Defining_an_event:

Defining an event
-----------------

Kasper events are defined as immutable, anemic objects, extending the **Event**  interface and
can optionally define metadata using the **@XKasperEvent** annotation, its class name ends with the
'**Event**' prefix (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends Event {
        private final KasperId userSource;
        private final KasperId userTarget;

        public UsersAreNowConnected(final KasperId userSource, final KasperId userTarget) {
            this.userSource = userSource;
            this.userTarget = userTarget;
        }

        public KasperId getUserSource() {
            return this.userSource;
        }

        public KasperId getUserTarget() {
            return this.userTarget;
        }
    }



..  _Defining_an_domain_event:

Defining a domain event
-----------------------

If your event is originated from a domain (not a management event or other out-of-domain generated event), you have to preferably mark your events with the **DomainEvent** interface.

**A major part of your events should be domain events**

**usage**

.. code-block:: java
    :linenos:

    public interface UsersEvent extends DomainEvent<UserDomain> { }

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends Event implements UsersEvent {
        private final KasperId userSource;
        private final KasperId userTarget;

        public UsersAreNowConnected(final KasperId userSource, final KasperId userTarget) {
            this.userSource = userSource;
            this.userTarget = userTarget;
        }

        public KasperId getUserSource() {
            return this.userSource;
        }

        public KasperId getUserTarget() {
            return this.userTarget;
        }
    }


..  _Defining_a_domain_entity_event:

Defining a domain entity event
------------------------------

As a vast majority of cases, events have as major concern a specific domain entity, the interface **EntityEvent<Domain>** is
provided, with default implementations **EntityCreatedEvent**, **EntityUpdatedEvent** and **EntityDeletedEvent**.

**usage**

.. code-block:: java
    :linenos:

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends EntityCreatedEvent<MyDomain> {
        private final KasperId userTarget;

        public UsersAreNowConnected(final KasperId userSource,
                                    final KasperId userTarget) {
            super(userSource);
            this.userTarget = userTarget;
        }

        public KasperId getUserSource() {
            return this.getEntityId();
        }

        public KasperId getUserTarget() {
            return this.userTarget;
        }
    }


..  _Defining_an_event_listener:

Defining an event listener
--------------------------

An event listener "just" listens for events..

A Kasper event listener have to extend the **EventListener<Event>**, declaring its owning domain using the **@XKasperEventListener** annotation, and have a name ending with '**EventListener**' (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperEventListener( domain = MyDomain.class, description = "Send a email when two users are connected" )
    public class SendAnEmailWhenTwoUsersAreConnectedEventListener extends EventListener<UsersAreNowConnectedEvent> {

        @Override
        public void handle(final UsersAreNowConnectedEvent event) {
            MailService.send(event.getUserSource(), event.getUserTarget(), MailTemplates.USERS_ARE_NOW_CONNECTED);
            MailService.send(event.getUserTarget(), event.getUSerSource(), MailTemplates.USERS_ARE_NOW_CONNECTED);
        }

    }

A common job of event listeners is to send new commands to the command gateway concerning its domain or another.
You can access the **getCommandGateway()** getter in order to retrieve an (optional) reference to the command gateway.


..  _Defining_a_saga:

Defining a Saga
--------------------------

A Saga is a special type of Event Listener that manages a single business transaction running for seconds, days or weeks.
That means that a saga has got a maintained state in order to manage the transaction.
All sagas must implement the **Saga** interface, declaring its owning domain using the **@XKasperSaga** annotation, and have a name ending with ‘Saga‘ (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperSaga(domain = MyDomain.class, description = "my domain saga" )
    public class MyDomainSaga implements Saga {

        private static final Logger LOGGER = getLogger(MyDomainSaga.class);

        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
            return Optional.absent();
        }

        @XKasperSaga.Start(getter = "getEntityId")
        public void start(final StarterEvent event){
            LOGGER.info("The saga has started with id : "+event.getEntityId());
        }

        @XKasperSaga.Step(getter = "getEntityId")
        @XKasperSaga.Schedule(delay = 15, unit = TimeUnit.MINUTES, methodName = "helloWorld")
        public void step(final stepEvent event){
            LOGGER.info("The step has been triggers : "+event.getEntityId());
        }

        @XKasperSaga.End(getter = "getEntityId")
        public void end(final EndEvent event){
            LOGGER.info("The saga has ended : "+event.getEntityId());
        }

        private void helloWorld(){
            LOGGER.info("A HelloWorld call has been triggered by scheduler");
        }
    }

**Saga Annotations**

First of all, when a Saga is created when invoking a **@XKasperSaga.Start** annotated Event Handler, it is automatically associated with the property identified by the id 'getter' method.
The events ids should then be the same in order to be able to retrieve the Saga. You can also implement a **SagaIdReconciler** if you have to make some transformations with ids.
Then, you can define some steps methods (**@XKasperSaga.Step**) which can be tunned by the **@XKasperSaga.Schedule** annotation in order to trigger saga's method calls.
The  **@XKasperSaga.Schedule** annotation can be put on a **@XKasperSaga.Step** or on a **@XKasperSaga.Start** step but not on a **@XKasperSaga.End**.
You can define several **@XKasperSaga.End** steps to end the saga.
Finally, you will end the saga by an Event handler annotated with **@XKasperSaga.End**. It will automatically delete the Saga and all associated Scheduled method calls.

**Saga Repository**

The **SagaRepository** is responsible for storing and retrieving Sagas, for use by the **SagaExecutor**. It is capable to retrieve specific Saga instances using the Event specified identifier.
We provide an abstract class **BaseSagaRepository** which is easy to extends and a default **InMemorySagaRepository** which stores the saga in the JVM.

**Saga Scheduler**

For saga's methods call scheduling, we are using **Quartz** scheduler. The default implementation is an inMemory but you can easily custom the **SchedulerFactory** to persist the scheduled steps.

**Exceptions**

You have to manage all exceptions within a step in order to keep the Saga's state safe.


..  _Understand_the_hierarchies_of_events:

Understand the hierarchies of events
------------------

Kasper event listeners are parameterized with an event class, this imply they can listen for events hierarchies.

You are then heavily encouraged to define at least one global event interface for each domain.

Sub-hierarchies can then also be created for functional sub-areas, operations, etc..

**ex** :

- Event
    - CarsEvent (+ DomainEvent)
        - CarsOperationFinishedEvent
        - CarLoansEvent
            - CarLoanCreatedEvent
            - CarLoanCancelledEvent
            - CarLoanUpdatedEvent
            - CarLoanFinishedEvent (+ CarsOperationFinishedEvent)
        - CarWashEvent
            - CarWashOrderedEvent
            - CarWashCancelledEvent
            - CarWashedEvent (+ CasOperationFinishedEvent)

A listener can then listen for all finished operations on cars, for all car loan events, all car wash events or even all events occured on the Cars domain.


..  _Use_events_to_decouple_your_legacy:

Use events to decouple your legacy
----------------------------------

Events can be emitted to the platform using **POST** or **PUT** requests, there are no query parameters, everything is in the body.
Actually only json content is supported as input and output.

To enable Command exposition register **HttpEventExposer** servlet, it will then use **DomainLocator** to locate each command handler.

Warning: Domain events exposing is an anti-pattern of the platform's spirit in itself, this endpoint is provided as a migration helper when dealing with a
legacy platform allowing a smooth decoupling : the legacy platform can then send domain events in place of the not-yet-implemented platform's
domain to come.

see :doc:`../3_defining_a_platform/8_using_http_exposition`
