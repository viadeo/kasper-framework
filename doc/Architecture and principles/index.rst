
Architecture & principles
=========================

.. image:: ../img/kasper-platform.png
    :scale: 45%
    :align: center

Kasper defines the way the different architecture components must be written and interacts and provides abstract classes for them and some full implementations for infrastructure components.

Kasper defines five responsibility areas :

* The **COMMAND** area is responsible for :
    * defining and handling action commands
    * combining concepts and relations (DDD entities) together with business rules in order to model the business domain
    * generating domain events
    * persisting the entities
    * interact with business indexes for validation purposes

* The **QUERY** area is responsible for :
    * listening for domain events, denormalizing them to a dedicated storage or sending them to another subsystem (mail delivery for instance)
    * defining and handling data queries
    * indexing events into dedicated storage backends

* The **NEUTRAL** area provides some platform services to the two other layers, such as :
    * security mechanisms (Authentication/Identifcation) and ACLs (Authorizations)
    * asynchronous errors events and other events storage for the users of the system (either final ones or system users)

* The **PRODUCT**, or platform client :
    * responsible to send commands to the platform, mainly asynchronously, and to manage user flow and interactions (task oriented)
    * responsible for sending data requests to the platform

* The **EVENT PLATFORM** is responsible for dealing for the event communication, persistence and storage, it deals with three kind of events :
    * APPLICATION events are events from the product
    * DOMAIN events are events sent by the domain implementation of the COMMAND area
    * SYSTEM events are events sent by the infrastructure

The implementation of the event platform is not fully part of the Kasper framework, which only manages with APPLICATION and DOMAIN events.


Using Kasper you'll first need to understand some major facts :

1. Your model is split into two parts :
    * the **write model** (command), enclosing the main part of your business model (validation, business relations, services, events, inter-domain communications)
    * the **read model** (query), enclosing data transfer logic and indexation policies
2. The write model (command) is known to have **ACID** transactional properties (http://en.wikipedia.org/wiki/ACID)
3. The read model try to prepare the data to be read with a minimum of computations and is known to be **BASE** (Basically Available, Soft state, Eventual consistency : http://en.wikipedia.org/wiki/Eventual_consistency)
4. The entity store, the business indexes and query indexes are logical components, nothing is said by Kasper about their real implementation, in the worst case
   these three stores can be implemented by the same backend with the same data, but you'll loose a major part of the CQRS pattern.. In the best case these three
   components are implemented by different data stores, or at least with different data.
5. Query indexes are generally multiples, each index owned by a specific domain. In a idealistic way each query have it's own index. If for infrastructure constraints
   you have to share indexes between several domains, this index have to be owned by one domain and used by others under schema policy responsibility of the first one.
6. Even if the code of the commands and queries could be shared within one folder hierarchy, always consider command and query areas as separate applications communicating
   through events, imagine that the command area and query area are implemented by different companies for instance.
7. The interface of a domain is the specification of the **commands, queries and eventsi (C/Q/E)**. The internal model of a domain will never be shared between different domains.
8. Separation of concerns between command and query areas could eventually be broken by the infrastructure layer (even if deprecated), but never by the code itself.
9. Commands, queries, events and even the model must be developed with the product in mind, at each step, choose carefully the names of your software components with
   this idea always in mind, maintain the coherency between the code and the specification, they are different implementations of the same thing, expressed with
   an ubiquitous language shared between the product and technical teams (**Intention-revealing interfaces**).
10. Data persistence is only required while we are not implementing our products on a Turing machine with infinite memory and storage. Think your model as a way to express
    the needs of your product first, then think your data persistence mechanisms at the end, when all other things has been done, it must be the last thing you want
    to think about.

Recommended modules split
-------------------------

A Kasper domain definition can be encapsulated in only one module, however Kasper framework encourage developers to split their domains in several modules, ensuring strict respect of the domain entities usage, anemic commands and events and atomic dependencies between domains.

The following diagram illustrates the five logical Kasper modules by domain with their dependency relations :

.. image:: ../img/modules_split_1.png
    :align: center
    :scale: 80%

The domain exposition handlers are then clearly identified as being the 'commands', 'events' and 'query' modules :

.. image:: ../img/modules_split_2.png
    :align: center
    :scale: 80%

An alternative, for those who dislike too many modules can be the following split :

.. image:: ../img/modules_split_3.png
    :align: center
    :scale: 80%

BUT, you'll have to be perhaps a bit more strict on the usages of classes depedencies in order to not allow unwanted dependencies :

    * Event listeners should only know commands, it is allowed to make a dependency to domain if domain services are used directly, but be careful..
    * Command handlers should only know domain classes.. and commands
    * Commands, and events must not have any dependency to other domain classes
    * Commands must be known only by command handlers
    * DQOs and DTOs should only be used by query services
    * **NEVER, NEVER, NEVER, NEVER** allow usage of DOMAIN (COMMAND) module by QUERY module
    * **NEVER, NEVER, NEVER, NEVER** allow usage of QUERY module by DOMAIN module
    * **NEVER, NEVER, NEVER, NEVER** allow usage of QUERY or DOMAIN modules by the API module

