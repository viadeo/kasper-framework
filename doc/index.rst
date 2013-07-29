
Kasper is the treasure keeper
=============================

.. topic:: Wikipedia - Kasperle

    Kasper (known as Kasperl and Kasperle in southern Germany and Austria) is the hero of German puppet theater. 
    The name Kasper probably comes from the ancient Persian meaning "keeper of the treasure." Tradition holds 
    one of the three Magi who visited the Christ Child was named Caspar. This character also existed in the 
    mystery plays of the medieval Church.

Kasper is the Viadeo's new platform architecture, **this is the engine framework of this platform**.

Kasper framework is based on several key components of the CQRS `Axon Framework`_ .

.. _Axon Framework: http://www.axonframework.org/

It acts as a skeleton and nervous system, an efficient modeling framework and infrastructure-as-a-layer bridge
in order to implement or (re-)enforce several key concepts :

* Separation of concerns between the modeling of the functional commands and domains, business rules, and query optimizations
* Clear separation of distinct expert areas as domain modeling, data persistence, authentication, identification, authorization, events handling or query optimization
* Automated platform exposition and automated documentation

Kasper relies on several software and architecture patterns :

* Domain-Driven Design (DDD) with an additional Entity-Relation modeling layer and reinforcement of the business rules pattern of Specification
* Command & Query Responsbility Segregation (CQRS) pattern
* Event-sourcing (ES) pattern, partially applied
* Entity-Relationship model in order to re-enforce data modelisation and analysis

Kasper platform is built as an association of several main components :

* Two directly exposed gateways : the Command gateway and the Query gateway
* An automated HTTP interface
* A Command Bus and an Event Bus
* A security manager (SSO + Identity + ACL)
* An entity store, backed by the Repository pattern (DDD)
* An event log

Contents
========

.. toctree::
    :maxdepth: 2

    Architecture and principles/index
    Quickstart
    Develop with Kasper/index
    Auto-exposition
    Auto-documentation
    Kasper java client

Indices and tables
==================

* :ref:`genindex`
* :ref:`search`

