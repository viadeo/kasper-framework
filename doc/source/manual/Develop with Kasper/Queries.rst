
CQRS: Queries
=============



Implementing queries consists on implementing four Kasper components :

- one **event listener** used to listen for accurate events and index data
- one **query** used by the client to send a request to the platform
- one **query response** (with result) used by the platform to send responses back to the client
- one **query handler** which query the data according to the received query and sends back a response

Kasper does not say anything about your indexation process, but you are heavily encouraged to :

- always provide one full and one incremental strategy for all your indexes
- prefer event listeners to implement your incremental strategy

About implementing event listeners, see :ref:`events`.

Kasper queries uses the **Command pattern**, the client does not ask a service for data, it sends queries to the
platform and expects for a response with result.

Queries
-------

A Kasper query is an immutable, anemic object used to request the platform for data, its class name
ends with the suffix '**Query**' (recommended).

**A Query is part of a domain API**.

It is used by the client in order to send requests and by the query handler to understand
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

- **OrderedQuery** can be implemented when the response can be ordered
- **PaginatedQuery** can be implemented when the response can be paginated

Query response results
----------------------

A Kasper query response result is an immutable, anemic object used by a query handler to send back data
to the requesting client, it ends with the suffix '**QueryResult**' (recommended).

**A Query response result is part of a domain API**.

A Kasper query response has to implement the interface **QueryResult** and can optionally defines some metadata
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

.. hint::
    The interface **QueryEntityResult** and proposed default implementation **AbstractQueryEntityResult** should be used for each
    result which is an entity (with an id, a type and optionally but preferably a last modification time)

The interface **QueryCollectionResult** can be used to return a list of some other unit response results.

The abstract class **AbstractQueryCollectionResult** is provided as a default implementation of the list methods
required by the **QueryCollectionResult** interface.

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryResult( description = "A List of things" )
    public class ThingsListQueryResult extends AbstractQueryCollectionResult<ThingsQueryResult> {
        // Nothing more needs to be declared
    }

Some interfaces are available as a standard way to add some features to the query response results :

- **OrderedQueryResult** can be implemented when the response result can be ordered
- **PaginatedQueryResult** can be implemented when the response result can be paginated

Query services
--------------

A Kasper query handler is I/O component using a **Query** as input and responsible to return a **QueryResult**.

**A Query service is part of the QUERY architectural area**.

It has to implement the **QueryHandler<Query, QueryResult>** interface and specify its owning domain with the **@XKasperQueryHandler**
annotation and ends with the '**QueryHandler**' suffix (recommended).

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryHandler( domain = ThingsDomain.class )
    public class GetThingsQueryHandler implements QueryHandler<GetThingsQuery, ThingsListQueryResult> {

        @Override
        public QueryResponse<ThingsListQueryResult> retrieve(final QueryMessage<GetThingsQuery> message) throws KasperQueryException {
            ...
        }

    }

The **AbstractQueryHandler** abstract class is provided in order to ease the extraction of the query from the message
when other message informations are not required :

**usage**

.. code-block:: java
    :linenos:

    @XKasperQueryHandler( domain = ThingsDomain.class )
    public class GetThingsQueryHandler extends AbstractQueryHandler<GetThingsQuery, ThingsListQueryResult> {

        @Override
        public QueryResponse<ThingsListQueryResult> retrieve(final GetThingsQuery query) throws KasperQueryException {
            ...
        }

    }

QueryResponse Caching
---------------------

Kasper framework provides a way to cache query responses based on the submitted query, the cache is enabled per QueryHandler and is disabled by default.

It is based on **JSR 107 - JCache** for selecting a cache implementation. By default no cache implementation is provided by the framework
you can use any implementation of JCache (for example using ehcache-jcache).

To enable the cache for a query handler with default configuration, just put **@XKasperQueryCache** annotation:

.. code-block:: java
    :linenos:

    @XKasperQueryHandler( domain = AwesomeDomain.class, cache = @XKasperQueryCache )
    public class GetNiceDataQueryHandler extends AbstractQueryHandler<GetNiceDataQuery, NiceDataQueryResult> {
        ...
    }

The default behaviour will be to use the **QueryAttributesKeyGenerator** for computing the key of the query and use a ttl of one hour.

**QueryAttributesKeyGenerator** is using the hashcode of your query if no key is defined, otherwise it will combine the hashcode of the keys.

Use only someField and anotherField in the generated key and have a ttl of 1 minute:

.. code-block:: java

    @XKasperQueryCache(keys = {"someField", "anotherField"}, ttl=60)

You can also have custom KeyGenerators, to do so just implement **QueryCacheKeyGenerator** and enable it:

.. code-block:: java

    @XKasperQueryCache( keyGenerator = MyKeyGenerator.class )


Service filters
---------------

Kasper framework allows you to define filters on Query services.

These filters can be of two kind :

- **Query filters** : can be used to mutate the query before its processing by the service
- **Response filters** : can be used to mutate the response generated after processing of the query by the service

In order to define a service filter, you have to :

1. Implement **QueryFilter** or **ResponseFilter** interfaces (Kasper core)
2. Add the annotation **@XKasperServiceFilter**, where you can define an optional name for your filter

ex :

**ValidateIdQueryFilter.class** :

.. code-block:: java
    :linenos:

    @XKasperServiceFilter( name = "ValidateUniverseId" )
    public class ValidateIdQueryFilter implements QueryFilter<HasAnIdQuery> {

        @Override
        public HasAnIdQuery filter(final Context context, final HasAnIdQuery query) throws KasperQueryException {
            if (query.id > 42) {
                throw new KasperQueryException("The id cannot be greater than 42 !");
            }
            return query;
        }

    }

A filter can be defined global (set the global flag (**global = true**) on the annotation).

**IdEraserResponseFilter.class** :

.. code-block:: java
    :linenos:

    @XKasperServiceFilter( global = true ) // Will be applied to all query handlers
    public class IdEraserResponseFilter implements ResponseFilter<HasAnIdResult> {

        @Override
        public QueryResponse<HasAnIdResult> filter(final Context context, final QueryResponse<HasAnIdResult> dto) throws KasperQueryException {
            QueryResponse<HasAnIdResult res = dto; /* Result DTO should be immutable */
            if (!res.isError() && HasAnIdResult.class.isAssignableFrom(dto.getResult())) {
                res = QueryResponse.of(new HasAnIdResult.Builder(dto.getResult()).setId("").build());
            }
            return res;
        }

    }

Global filters will be applied after user-defined filters, and user-defined filters are applied in the order of their definition within the annotation.

A global service filter can be domain-sticky (only executed on services of the specified domain) using the **domain** field of the
**@XKasperQueryHandler** annotation.

A non-global filter can then be associated to one or several services using the **@XKasperQueryHandler** annotation,
filling the 'filters' field.

**GetThingsQueryHandler.class** :

.. code-block:: java
    :linenos:

    @XKasperQueryHandler( ... , filters = ValidateIdQueryFilter.class )
    public class GetThingsQueryHandler extends AbstractQueryHandler<GetThingsQuery, ThingsListQueryResult> {

        @Override
        public QueryResponse<ThingsListQueryResult> retrieve(final GetThingsQuery query) throws KasperQueryException {
            ...
        }

    }


