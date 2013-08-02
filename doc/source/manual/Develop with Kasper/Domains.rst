DDD: Domains
============

Kasper domains can be understood as DDD bounded contexts, it's related to s specific functional area and is 
generally under the responsibility of a unique team.

.. topic:: Wikipedia - Bounded context

    Multiple models are in play on any large project. Yet when code based on distinct models is combined, software
    becomes buggy, unreliable, and difficult to understand. Communication among team members becomes confusing. It
    is often unclear in what context a model should not be applied.
    Therefore: Explicitly define the context within which a model applies. Explicitly set boundaries in terms of
    team organization, usage within specific parts of the application, and physical manifestations such as code
    bases and database schemas. Keep the model strictly consistent within these bounds, but don’t be distracted
    or confused by issues outside.

A domain is separated into several logical parts, dependencies are symbolized here as arrows :

.. image:: ../img/modules_split_1.png
    :align: center
    :scale: 80%

So a domain :

* is split between the **COMMAND** and **QUERY** architectural areas
* offers an API composed of :
    * the **commands** it can handle
    * the **queries** it can answer
    * the **query results** it can send
    * the **events** it will emit
* cannot have access to any part of another domain other than its api components (C/Q/E)
* represent an atomic functional area, generally handled by only one close team

Define a Kasper domain
----------------------

A domain is generally defined as an empty class, implementing the '**Domain**' interface and
annotated with the **@XKasperDomain** annotation.

**A domain is part of a domain API**.

**usage**

.. code-block:: java
    :linenos:

    @XKasperDomain( prefix = "th", label = "The Things domain" )
    public class ThingsDomain implements Domain { }

This class will be used by the other Kasper components annotations as a **logical aggregator**.


Inter-domains considerations
----------------------------

TODO
