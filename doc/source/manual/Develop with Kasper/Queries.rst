
CQRS: Queries
=============



Implementing queries consists on implementing four Kasper components :

- one **event listener** used to listen for accurate events and index data
- one **query** used by the client to send a request to the platform
- one **query result** used by the platform to send results back to the client
- one **query service** which query the data according to the received query and sends back a result

Kasper does not say anything about your indexation process, but you are heavily encouraged to :

- always provide one full and one incremental strategy for all your indexes
- prefer event listeners to implement your incremental strategy

About implementing event listeners, see :ref:`events`.

Kasper queries uses the **Command pattern**, the client does not ask a service for data, it sends queries to the
platform and expects for a result.

Queries
-------

A Kasper query is an immutable, anemic object used to request the platform for data, its class name
ends with the suffix '**Query**' (recommended).

**A Query is part of a domain API**.

It is used by the client in order to send requests and by the query service to understand
the request and filter the indexed data.

A Kasper query has to implement the interface **Query** and can optionally defines some metadata
using the **@XKasperQuery** annotation.

**usage**

.. code-block:: java
    :linenos:

    @XKasperQuery( description = "Get some things" )
    public class GetThingsQuery implements Query {

        public static final int DEFAULT_NUMBER_OF_THINGS = 10;

        private final int numberOfThings;

        public void GetThingsQuery() {
            this.numberOfThings = DEFAULT_NUMBER_OF_THINGS;
        }

        public void GetThingsQuery(final int numberOfThings) {
            this.numberOfThings = numberOfThings;
        }

        public int getNumberOfThings() {
            return this.numberOfThings;
        }

    }

Some interfaces are available as a standard way to add some features to the query :

- **OrderedQuery** can be implemented when the result can be ordered
- **PaginatedQuery** can be implemented when the result can be paginated

Query results
-------------

A Kasper query result is an immutable, anemic object used by a query service to send back data
to the requesting client, it ends with the suffix '**QueryResult**' (recommended).

**A Query result is part of a domain API**.

A Kasper query result has to implement the interface **QueryResult** and can optionally defines some metadata
using the **@XKasperQueryResult** annotation.

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryResult( description = "A simple thing" )
    public class ThingsQueryResult implements QueryResult {
        private final String name;

        public ThingsQueryResult(final String nameOfThing) {
            this.name = nameOfThing;
        }

        public String getName() {
            return this.name;
        }
    }

The interface **QueryCollectionResult** can be used to return a list of some other unit results.

The abstract class **AbstractQueryCollectionResult** is provided as a default implementation of the list methods
required by the **QueryCollectionResult** interface.

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryResult( description = "A List of things" )
    public class ThingsListQueryResult extends AbstractQueryCollectionResult<ThingsQueryResult> {
        // Nothing more needs to be declared
    }

Some interfaces are available as a standard way to add some features to the query result :

- **OrderedQueryResult** can be implemented when the result can be ordered
- **PaginatedQueryResult** can be implemented when the result can be paginated

Query services
--------------

A Kasper query service is I/O component using a **Query** as input and responsible to return a **QueryResult**.

**A Query service is part of the QUERY architectural area**.

It has to implement the **QueryService<Query, QueryResult>** interface and specify its owning domain with the **@XKasperQueryService**
annotation and ends with the '**QueryService**' suffix (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryService( domain = ThingsDomain.class )
    public class GetThingsQueryService implements QueryService<GetThingsQuery, ThingsListQueryResult> {

        @Override
        public ThingsListQueryResult retrieve(final QueryMessage<GetThingsQuery> message) throws KasperQueryException {
            ...
        }

    }

The **AbstractQueryService** abstract class is provided in order to ease the extraction of the query from the message
when other message informations are not required :

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryService( domain = ThingsDomain.class )
    public class GetThingsQueryService extends AbstractQueryService<GetThingsQuery, ThingsListQueryResult> {

        @Override
        public ThingsListQueryResult retrieve(final GetThingsQuery query) throws KasperQueryException {
            ...
        }

    }


Service filters
---------------

Kasper framework allows you to define filters on Query services.

These filters can be of two kind :

- **Query filters** : can be used to mutate the query before its processing by the service
- **Result filters** : can be used to mutate the result generated after processing of the query by the service

In order to define a service filter, you have to :

1. Implement **QueryFilter** or **ResultFilter** interfaces (Kasper core)
2. Add the annotation **@XKasperServiceFilter**, where you can define an optional name for your filter

ex :

**ValidateIdQueryFilter.class** :

.. code-block:: java
    :linenos:

    @XKasperServiceFilter( name = "ValidateUniverseId" )
    public class ValidateIdQueryFilter implements QueryFilter {

        @Override
        public void filter(final Context context, final Query query) throws KasperQueryException {
            if (HasAnIdQuery.class.isAssignableFrom(query)) {
                if (((HasAnIdQuery) query).id > 42) {
                    throw new KasperQueryException("The id cannot be greater than 42 !");
                }
            }
        }

    }

A filter can be defined global (set the global flag (**global = true**) on the annotation).

**IdEraserResultFilter.class** :

.. code-block:: java
    :linenos:

    @XKasperServiceFilter( global = true ) // Will be applied to all query services
    public class IdEraserResultFilter implements ResultFilter {

        @Override
        public void filter(final Context context, final Result dto) throws KasperQueryException {
            if (HasAnIdResult.class.isAssignableFrom(dto)) {
                ((HasAnIdResult) dto).id = "";
            }
        }

    }

Global filters will be applied after user-defined filters, and user-defined filters are applied in the order of their definition within the annotation.

A global service filter can be domain-sticky (only executed on services of the specified domain) using the **domain** field of the
**@XKasperQueryService** annotation.

A non-global filter can then be associated to one or several services using the **@XKasperQueryService** annotation,
filling the 'filters' field.

**GetThingsQueryService.class** :

.. code-block:: java
    :linenos:

    @XKasperQueryService( ... , filters = ValidateIdQueryFilter.class )
    public class GetThingsQueryService extends AbstractQueryService<GetThingsQuery, ThingsListQueryResult> {

        @Override
        public ThingsListQueryResult retrieve(final GetThingsQuery query) throws KasperQueryException {
            ...
        }

    }


