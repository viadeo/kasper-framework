
Implementing a domain
========================

A domain is generally defined as an empty class, implementing the `Domain` interface and annotated with the `@XKasperDomain` annotation.

A `Domain` must be a part of a domain API in order to be accessible by all components of our domain.

**usage**

.. code-block:: java
    :linenos:

    @XKasperDomain( prefix = "th", label = "The Things domain" )
    public class ThingsDomain implements Domain { }

This class will be used by the other Kasper components annotations as a **logical aggregator**.