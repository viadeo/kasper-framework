.. Kasper framework documentation master file, created by
   sphinx-quickstart on Mon Jun 10 16:22:05 2013.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Kasper is the treasure keeper
=============================

Kasper is the Viadeo's new platform architecture, this is the engine framework of this platform.

It acts as a skeleton and nervous system, an efficient modeling framework and infrastructure-as-a-layer bridge
in order to implement or (re-)enforce several key concepts :

* Separation of concerns between the modeling of the functional commands and domains, business rules, and query optimizations
* Clear separation of distinct expert areas as domain modeling, data persistence, authentication, identification, authorization, events handling or query optimization
* Automated platform exposition and automated documentation

Kasper relies on several software and architecture patterns :

* Domain-Driven Design (DDD) with an additional Entity-Relation modeling layer and reinforcement of the business rules pattern of Specification
* Command & Query Responsbility Segregation (CQRS) pattern

Kasper platform is built as an association of several main components :

* Two directly exposed gateways : the Command gateway and the Query gateway
* An automated HTTP interface
* A Command Bus and an Event Bus
* A security manager (SSO + Identity + ACL)
* An entity store, backed by the Repository pattern
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

