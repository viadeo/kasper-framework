.. _getting-started:

Getting Started
=============================

Kasper is the Viadeo's new platform architecture, **this is the engine framework of this platform**.

Kasper framework is based on several key components of the CQRS framework `Axon <http://www.axonframework.org>`_ .

It acts as a skeleton and nervous system, an efficient modeling framework and infrastructure-as-a-layer bridge
in order to implement or (re-)enforce several key concepts :

* Separation of concerns between the modeling of the functional commands and domains, business rules, and query optimizations
* Clear separation of distinct expert areas as domain modeling, data persistence, authentication, identification, authorization, events handling or query optimization
* Automated platform exposition and automated documentation

Key patterns
------------

Kasper relies on several software and architecture patterns :

* Domain-Driven Design (DDD)
* Command & Query Responsbility Segregation (CQRS) pattern
* Event-sourcing (ES) pattern, partially applied
* Entity-Relationship model in order to re-enforce data modelisation and analysis

Main components
---------------

The Kasper platform is built as an association of several main components :

* Two directly exposed gateways : the CommandGateway and the Query gateway
* An automated HTTP interface
* A Command Bus and an Event Bus
* A security manager (SSO + Identity + ACL)
* An entity store, backed by the Repository pattern (DDD)
* An event log

Quickstart
----------

Check :ref:`modules_split` and choose one module splitting strategy, then create your project hierarchy accordingly.

ex: minimal modules strategy : api/command/query

- **mycompany-mydomain-api**
    depends on :
        * kasper-api

- **mycompany-mydomain-command**
    depends on :
        * *mycompany-mydomain-api*
        * kasper-core

- **mycompany-mydomain-query**
    depends on :
        * *mycompany-mydomain-api*
        * kasper-core

- **mycompany-mywebapp**
    depends on :
        * *mycompany-mydomain-query*
        * *mycompany-mydomain-command*
        * kasper-platform (the Kasper platform, with resources discovery, binding mechanisms, ...)
        * kasper-exposition (web bindings, helpers, auto-exposition)
        * kasper-documentation (auto-documentation)

TODO: domain bootstrap helpers, webapp configuration,..

