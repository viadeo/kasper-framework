DDD: Domains modeling
=====================

Kasper domain modeling is heavily based on Domain-Driven Design paradigms.

.. topic:: Wikipedia - Domain-Driven design

    Domain-driven design (DDD) is an approach to develop software for complex needs by connecting the implementation to an evolving model. The premise of domain-driven design is the following:
    
    - Placing the project's primary focus on the core domain and domain logic.
    - Basing complex designs on a model of the domain.
    - Initiating a creative collaboration between technical and domain experts to iteratively refine a conceptual model that addresses particular domain problems.

    The term was coined by Eric Evans in his book of the same title.

    **Core definitions**

    **Domain**: A sphere of knowledge (ontology), influence, or activity. The subject area to which the user applies a program is the domain of the software.

    **Model**: A system of abstractions that describes selected aspects of a domain and can be used to solve problems related to that domain.

    **Ubiquitous Language**: A language structured around the domain model and used by all team members to connect all the activities of the team with the software.

    **Context**: The setting in which a word or statement appears that determines its meaning.

.. image:: ../img/ddd-diagram.png
    :scale: 45%
    :align: center

.. contents::


Entities
--------

.. topic:: Wikipedia - DDD entity

    **Entity**: An object that is not defined by its attributes, but rather by a thread of continuity and its identity.

    *Example: Most airlines distinguish each seat uniquely on every flight. Each seat is an entity in this context. However, Southwest Airlines (or EasyJet/RyanAir for Europeans) does not distinguish between every seat; all seats are the same. In this context, a seat is actually a value object.*

DDD entities
^^^^^^^^^^^^

In Domain-Driven Design an entity is a key element of the model (with value objects and services). It is an object enclosing data and
business methods which allows to mutate the entity or export its data in any pertinent form.

*ex: an Order, an OrderLine*

DDD aggregates
^^^^^^^^^^^^^^

.. topic:: Wikipedia - DDD aggregate

    **Aggregate**: A collection of objects that are bound together by a root entity, otherwise known as an aggregate root. The aggregate root guarantees the consistency of changes being made within the aggregate by forbidding external objects from holding references to its members.
    *Example: When you drive a car, you do not have to worry about moving the wheels forward, making the engine combust with spark and fuel, etc.; you are simply driving the car. In this context, the car is an aggregate of several other objects and serves as the aggregate root to all of the other systems.*

.. topic:: Martin Fowler - DDD aggregate

    A DDD aggregate is a cluster of domain objects that can be treated as a single unit. An example may be an order and its line-items, these will be separate objects, but it's useful to treat the order (together with its line items) as a single aggregate.

    An aggregate will have one of its component objects be the aggregate root. Any references from outside the aggregate should only go to the aggregate root. The root can thus ensure the integrity of the aggregate as a whole.

    Aggregates are the basic element of transfer of data storage - you request to load or save whole aggregates. Transactions should not cross aggregate boundaries.

*ex: an Order, composed of several Order lines*

Instead of letting the domain user to create OrderLine instances independently and provides it to the Order objects, we will consider Order as the root of an aggregate.

This aggregate, managed by its root entity, will concentrate any operation that can be offered on its enclosing entities. Adding an OrderLine is then an internal behaviour of the aggregate, but this last
entity is not known as-is by the domain clients.

Adding an OrderLine is in fact the action to add a number of products to the Order for instance.

Instead of calling *order.add(new OrderLine(3, myproductId))* we'll just call *order.add(3, myproductId)*.

Because OrderLines has no reason to exists without the Order itself, because the root of the aggregate will ensure coherency within the whole aggregate. A general
amount counter can then be maintained directly in the aggregate for instance, which will be persisted as an atomic object in the datastore.

Kasper E-R entities and aggregates
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Kasper re-inforce some of these aspects, it :

- generalize the **aggregate** as being **the only persistable entity** type (only aggregates can be persisted, no standalone entities)
- splits the entity component as besoin either a **Concept** or a **Relation**, defining an entity-relationship (E-R) model.


A **Concept** is any entity which does not represent a relation between two other entities.

A **Relation** is an entity which links, with meaning, two other Concept entities.

A component entity is an entity which is part of an aggregate but do not represent its root entity, it's an **aggregate component**.

And implements one of the two classes **Concept**, **Relation** declaring the owning domain
using the **@XKasperConcept** or **@XKasperRelation** annotations.

About writing aggregates :

.. toctree::
    :maxdepth: 2

    Aggregates

Concept
"""""""

A concept aggregate root is the base entity of a model. It is a persistable business entity as you can understand it in
many other object models.

*ex: a Car, a Member, a BlogPost, a Forum, a Job, ...*

As being the root of an aggregate, this kind of entity **can** hold references to component concepts and relations, but **must not
contain any direct reference to other aggregate roots without a intermediate relation entity**.

**An aggregate root in Kasper is not necessarily a composition of several entities**, it can just be a standalone object, persistable.

**usage**

.. code-block:: java
    :linenos:

    @XKasperConcept( domain = Vehicles.class, label = "A simple car" )
    public class Car extends Concept {

        private boolean started = true;

        // --

        public Car(final KasperId id) {
            apply(new ANewCarHasBeenCreatedEvent(id));
        }

        @EventHandler
        private void onCreated(final ANewCarHasBEenCreatedEvent event) {
            setId(event.getId());
        }

        // --

        public void startEngine() {
            apply(new EngineHasBeenStartedOnCarEvent());
        }

        @EventHandler
        private void onEngineStarted(final EngineHasBeenStartedOnCarEvent event) {
            if (this.started) {
                throw new CarIsAlwaysStarted();
            }
            this.started = true;
        }

        // --

        public void stopEngine() {
            apply(new EngineHasBeenStoppedOnCarEvent());
        }

        @EventHandler
        public void onEngineStopped(final EngineHasBeenStoppedOnCarEvent event) {
            if (!this.started) {
                throw new CarIsNotStarted();
            }
            this.started = false;
        }

    }

Relation
""""""""

A relation aggregate root is used to connect two concept aggregate roots with some optional metadata.

This implies that the two connected concept aggregate roots **can exists within the system independently, without this relation**.

A relation is by default unidirectional, a concept root A is connected to a concept root B. Adding the annotation
**@XBidirectional** to a relation makes it understandable as a bidirectional relation.

Kasper encourage to use a specific class names nomenclature for relations :

**<SourceRootConceptName>_<RelationVerb>_<TargetRootConceptName>**

*ex of relation verbs: connectedTo, friendWith, likedBy/likes, shares/sharedBy*


.. image:: ../img/ddd-kasper-root-relation.png
    :scale: 80%
    :align: center

**usage**

.. code-block:: java
    :linenos:

    @XBidirectional( verb = "likedBy" )
    @XKasperRelation( domain = MemberWall.class, verb = "likes", label = "A member liked an article" )
    public class Member_likes_Article extends Relation<Member, Article> {

        Member_likes_Article(final KasperId memberId, final KasperId articleId) {
            apply(new MemberLikedAnArticleEvent(memberId, articleId));
        }

        @EventHandler
        private void onCreated(final MemberLikedAnArticleEvent event) {
            setId(event.getMemberId(), event.getArticleId());
        }

    }

    @XKasperRelation( domain = MemberWall.class, label = "A member shares an article" )
    public class Member_shares_Article extends Relation<Member, Article> {

        Member_shares_Article(final KasperId memberId, final KasperId articleId) {
            apply(new MemberSharedAnArticle(memberId, articleId));
        }

        @EventHandler
        private void onCreated(final MemberSharedAnArticleEvent event {
            setId(event.getMemberId(), event.getArticleId());
        }

    }

TODO: add some stuff about verbs and ontologies, what will be took into account if empty, ...

Linked concepts
---------------

Never enclose a concept instance within another one :

.. code-block:: java
    :linenos:

    /* NEVER DO THIS */
    public class Member extends Concept {

        Member friendOf;

        ...

    }

Use instead a **LinkedConcept** which is a typed KasperId :

.. code-block:: java
    :linenos:

    /* NEVER DO THIS */
    public class Member extends Concept {

        LinkedConcept<Member> friendOf;

        ...

    }


Factories
---------

.. topic:: Wikipedia - DDD entities factory

    **Factory**: methods for creating domain objects should delegate to a specialized Factory object such that alternative implementations may be easily interchanged.

Kasper does not say anything about factories, unless it is domain components, located into your COMMAND modules.

You can create your factories as you want, but you are encouraged to implement the marker interface **EntityFactory<Entity>**
in order to mark and better identify all of your factories, this interface encourage the use of the **Builder pattern**.

You are encouraged to add the suffix **Factory** to your class names.

**usage**

.. code-block:: java
    :linenos:

    public class MemberFactory implements EntityFactory<Member> {

        private final MemberEntity entityFromLegacy;
        private final KasperId id;

        private int age = 0;

        public MemberFactory(final MemberEntity entityFromLegacy, final KasperId id) {
            this.entityFromLegacy = checkNotNull(entityFromLegacy);
            this.id = checkNotNull(id);
        }

        public void age(final int age) {
            this.age = age;
        }

        @Override
        public Member build(final Context context) {
            final Mamber ret = new Member(this.id, this.entityFromLegacy.getName());
            if (0 != this.age) {
                ret.setAge(this.age);
            }
            return ret;
        }

    }

Repositories
------------

.. topic:: Wikipedia - DDD repository

    **Repository**: methods for retrieving domain objects should delegate to a specialized Repository object such that alternative storage implementations may be easily interchanged.

A DDD repository is used to manage with entities persistence, and as the only persistable entities in Kasper framework are the aggregate roots then Kasper repositories are
bound to a specific aggregate root.

In order to create a Kasper repository you have to extend **Repository<AggregateRoot>** annotating it with the
**@XKasperRepository** annotation.

**usage**

.. code-block:: java
    :linenos:

    @XKasperRepository( description = "Stores a Member into an SQL datastore" )
    public class MemberRepository extends Repository<Member> {

        private static final String REQ_SELECT = "SELECT name FROM Member WHERE id = %d and version = '%s'";
        private static final String REQ_INSERT = "INSERT INTO Member VALUES(%d, '%s', '%s')";
        private static final String REQ_DELETE = "DELETE FROM Member WHERE memberId = %d AND version = '%s'";

        @Override
        protected Optional<Member> doLoad(final KasperID memberId, final Long expectedVersion) {
            final response = sql.selectFirst(String.format(REQ_SELECT, memberId, expectedVersion));
            if (null != response) {
                return Optional.of(new Member(memberId,
                                              expectedVersion,
                                              response.get('name')));
            }
            return Optional.absent();
        }

        @Override
        protected void doSave(final Member member) {
            sql.exec(String.format(REQ_INSERT, member.getIdentifier(),
                                               member.getVersion(),
                                               member.getName()));
        }

        @Override
        protected void doDelete(final Member member) {
            sql.exec(String.format(REQ_DELETE, member.getIdentifier(), member.getVersion()));
        }

    }

You can also add new public methods to this repository in order to access to your business indexes (logically hosted in your COMMAND architectural area). These methods can later be accessed from command handlers using (ClientRepository).business()

Repositories are then accessed using the methods **load()**, **get()**, **has()** or **add()**, generally in command handlers only.

The **load()** method loads an entity from the repository and marks it so it will be automatically saved (doSave() will be called on your aggregate) on unit of work commit, while the **get()** method only load the aggregate without marking it to be automatically saved. 

The **has()** method is not implemented by default, you'll have to override the **doHas()** method on your repository if you want this feature.

There is no **delete()** method on the repository. To delete an aggregate you have to create a specific method/handler on your loaded aggregate which calls the **markDeleted()** protected method internally. The aggregate is then marked as deleted, the **doDelete()** repository method will then be called once the unit of work is commited.
You are heavily encouraged to never delete data in your domains by just marking them as deleted. So in major cases doDelete() can just call doSave(), the loading of entities in Kasper repositories will take care of not loading deleted aggregates.

The doSave() method is use for entity creation AND update. If your backend needs to make the difference between
a creation or an update, you can :

- test **aggregate.getVersion()** for nullity in the **doSave()** method (newly created entities does not have a version)
- or implement the **doUpdate()** method, so entity creation will be automatically made calling **doSave()** and updates through **doUpdate()**

The **Repository** abstract class mut be considered as an **entity store** : the current state of entities is
stored, then events will be sent by the unit of work once entity is persisted. If you want to apply a real
event sourcing strategy, use instead the **EventSourcedRepository** supplying it an Axon **EventStore**.

Value objects
-------------

.. topic:: Wikipedia - DDD value object

    **Value Object**: An object that contains attributes but has no conceptual identity. They should be treated as immutable.

    *Example: When people exchange dollar bills, they generally do not distinguish between each unique bill; they only are concerned about the face value of the dollar bill. In this context, dollar bills are value objects. However, the Federal Reserve may be concerned about each unique bill; in this context each bill would be an entity.*

A value object is well.. just a value object..

But Kasper framework propose you two interfaces in order to better identify them and reinforce some good practices and
constraints.

**A value object is immutable.**

If you want to create a value object you can the interface **Value**.

The **Value** interface will force you to implement the **Serializable** interface and propose you to not miss the implementation
of the methods *toString()*, *hashCode()* and *equals()*.

**usage**

.. code-block:: java
    :linenos:

    public class WheelPosition implements Value {

        private static final enum AcceptedWheelPosition { FL, FR, BL, BR };

        private final AcceptedWheelPosition position;

        // -----

        private WheelPosition(final AcceptedWheelPosition position) {
            this.position = position;
        }

        // -----

        public static final frontLeft()  { return new WheelPosition(AcceptedWheelPosition.FL); }
        public static final frontRight() { return new WheelPosition(AcceptedWheelPosition.FR); }
        public static final backLeft()   { return new WheelPosition(AcceptedWheelPosition.BL); }
        public static final backRight()  { return new WheelPosition(AcceptedWheelPosition.BR); }

        // -----

        public boolean isFront() {
            return AcceptedWheelPosition.FL.equals(this.position)
                    || AcceptedWheelPosition.FR.equals(this.position);
        }

        public boolean isBack()  {
             return AcceptedWheelPosition.BL.equals(this.position)
                    || AcceptedWheelPosition.BR.equals(this.position);
        }

        public boolean isLeft()  {
             return AcceptedWheelPosition.FL.equals(this.position)
                    || AcceptedWheelPosition.BL.equals(this.position);
        }

        public boolean isRight() {
             return AcceptedWheelPosition.BR.equals(this.position)
                    || AcceptedWheelPosition.FR.equals(this.position);
        }

        // -----

        @Override
        public int hashCode() {
            return this.position.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            checkNotNull(other);
            if (this.getClass() != other.getClass()) {
                return false;
            }
            return this.position.equals((WheelPosition) other);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).addValue(this.position).toString();
        }

    }

Sometimes you just want to create a value object around one unique other type (primitive or not) and add management
methods to this enclosing value. Kasper framework propose you the **EnclosingValue** abstract class.

**usage**

.. code-block:: java
    :linenos:

    public class FirstName extends EnclosingValue<String> {

        public FirstName(final String firstName) {
            super(firstName);
        }

    }

Domain services
---------------

.. topic:: Wikipedia - DDD service

    **Service**: When an operation does not conceptually belong to any object. Following the natural contours of the problem, you can implement these operations in services.

Kasper does not say nothing about domain services, but propose the **DomainService** marker interface in order to ease their global
identification. A domain service can be either a COMMAND area service or a QUERY area service.

You are encouraged to add the suffix **Service** to your class names.

**usage**

.. code-block:: java
    :linenos:

    public class PremiumService implements DomainService {

        public void businessRule(final int productId, final String toto) {
            ...
        }

    }

A service can be used to share a business logic between a query and a command inside a domain.

A good pattern is to enclose the service calls inside your business models on command an query sides, keeping a correct OOP approach
while centralizing business algorithms :

.. edit this drawing here: https://docs.google.com/a/viadeoteam.com/drawings/d/1-wHZytGl6HkbwoOrBUG0Sir_oK2TyJKpLRCp3CjFE-k/edit?usp=sharing
.. image:: ../img/shared_services.png

