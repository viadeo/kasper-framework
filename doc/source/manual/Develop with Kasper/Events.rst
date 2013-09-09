.. _events:

CQRS: Events
============

Events are the nervous system of the Kasper platform.

Their counter-part are the event listeners, which can be defined either in the **COMMAND** or **QUERY**
architectural areas.

**Command listeners** receive events and send commands or interact with some domain services.

**Query listeners** denormalize events within query indexes.

Events
------

Kasper events are defined as immutable, anemic objects, implementing the **Event**  interface and
can optionally define metadata using the **@XKasperEvent** annotation, its class name ends with the
'**Event**' prefix (recommended).

A base implementation is provided by the **AbstractEvent** class (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends AbstractEvent {
        private final KasperId userSource;
        private final KasperId userTarget; 

        public UsersAreNowConnected(final Context context, final KasperId userSource, final KasperId userTarget) {
            super(context);

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

Domain events
^^^^^^^^^^^^^

If your event is originated from a domain (not a management event or other out-of-domain generated event), you have to
use the **DomainEvent** interface instead, provided with the default implementation **AbstractDomainEvent**.

**A major part of your events should be domain events**

**usage**

.. code-block:: java
    :linenos:

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends AbstractDomainEvent<MyDomain> {
        private final KasperId userSource;
        private final KasperId userTarget; 

        public UsersAreNowConnected(final Context context, final KasperId userSource, final KasperId userTarget) {
            super(context);

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


Domain entity events
^^^^^^^^^^^^^^^^^^^^

As a vast majority of cases, events have as major concern a specific domain entity, the interface **EntityEvent<Domain, Entity>** is
provided, with a default implementation **AbstractEntityEvent<Domain, Entity>**.

**usage**

.. code-block:: java
    :linenos:

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends AbstractEntityEvent<MyDomain, User> {
        private final KasperId userTarget; 

        public UsersAreNowConnected(final Context context, final KasperId userSource,
                                    final KasperId userTarget, final DateTime lastModificationDate) {
            super(context, userSource, lastModificationDate);
            this.userTarget = userTarget;
        }

        public KasperId getUserSource() {
            return this.getEntityId();
        }

        public KasperId getUserTarget() {
            return this.userTarget;
        }
    }

Entity-relationship events
^^^^^^^^^^^^^^^^^^^^^^^^^^

If your entity is an aggregate root (Concept or Relation), an entity which is persisted as a whole with its enclosed entities, you'll prefer to use either :

- **ConceptRootEvent<Domain, ConceptRoot>** interface, with its default implementation **AbstractConceptRootEvent<Domain, ConceptRoot>**.
- **RelationRootEvent<Domain, RelationRoot>** interface, with its default implementation **AbstractRelationRootEvent<Domain, RelationRoot>**.

**usage**

.. code-block:: java
    :linenos:

    @XKasperEvent( action = MyDomainActions.IS_CONNECTED_TO )
    public class UsersAreNowConnectedEvent extends AbstractConceptRootEvent<MyDomain, User> {
        private final KasperId userTarget; 

        public UsersAreNowConnected(final Context context, final KasperId userSource,
                                    final KasperId userTarget, final DateTime lastModificationDate) {
            super(context, userSource, lastModificationDate);
            this.userTarget = userTarget;
        }

        public KasperId getUserSource() {
            return this.getEntityId();
        }

        public KasperId getUserTarget() {
            return this.userTarget;
        }
    }

The interest of using these two last events is awesome as it allows a immediate graph-oriented denormalization of your events, for instance
as a default datastore in a graph database. **Do not negligate it !**


Event listeners
---------------

An event listener "just" listens for events..

A Kasper event listener have to extend the **AbstractEventListener<Event>**, declaring its owning domain using the **@XKasperEventListener** annotation,
and have a name ending with '**EventListener**' (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperEventListener( domain = MyDomain.class, description = "Send a email when two users are connected" )
    public class SendAnEmailWhenTwoUsersAreConnectedEventListener extends AbstractEventListener<UsersAreNowConnectedEvent> {

        @Override
        public void handle(final UsersAreNowConnectedEvent event) {
            MailService.send(event.getUserSource(), event.getUserTarget(), MailTemplates.USERS_ARE_NOW_CONNECTED);            
            MailService.send(event.getUserTarget(), event.getUSerSource(), MailTemplates.USERS_ARE_NOW_CONNECTED);            
        }

    }

A common job of event listeners is to send new commands to the command gateway concerning its domain or another.
If you use the **AbstractEventListener**, you can access the **getCommandGateway()** getter in order to retrieve an (optional)
reference to the command gateway.

Events hierarchies
------------------

Kasper event listeners are parameterized with an event class, this imply they can listen for events hierarchies.

You are then heavily encouraged to define at least one global event interface for each domain.

Sub-hierarchies can then also be created for functional sub-areas, operations, etc..

**ex** :

- Event
    - CarsOperationFinishedEvent
    - CarsEvent
        - CarLoansEvent
            - CarLoanCreatedEvent
            - CarLoanCancelledEvent
            - CarLoanUpdatedEvent
            - CarLoanFinishedEvent (+CarsOperationFinishedEvent)
        - CarWashEvent
            - CarWashOrderedEvent
            - CarWashCancelledEvent
            - CarWashedEvent (+CasOperationFinishedEvent)

A listener can then listen for all finished operations on cars, for all car loan events, all car wash events or even all
events occured on the Cars domain.

