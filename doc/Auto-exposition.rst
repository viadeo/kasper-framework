
=========================
Automated HTTP exposition
=========================

Kasper framework provides an exposition component allowing to automatically expose commands and queries.
Actually it is an HTTP exposition exchanging JSON messages + standard HTTP Headers and implemented via Java Servlets and a
databinding component mapping query strings and query POJOS. 

**Get it**:

.. code-block:: gradle
   
   Gradle : 
      'com.viadeo.kasper:kasper-web:KASPER_LATEST_VERSION'

   Maven: 
      <dependency>
         <groupId>com.viadeo.kasper</groupId>
         <artifactId>kasper-web</artifactId>
         <version>KASPER_LATEST_VERSION</version>
      </dependency>


-----
Goals
-----

During the first iteration on implementing kasper queries, we had to implement by hand each resource exposing a query.

Those implementations had following disadvantages: 

 * varying from team to team, thus being harder to use from a consumer perspective
 * a great portion of code was just boilerplate
 * decreased productivity as all the communication/exchange format had to be reimplemented for each new resource
 * no error handling

Platform teams were spending precious time on doing all that, so to improve productivity and make everyones life 
easier we implemented all that exposition layer in kasper framework.

 * Do all the work of exposing queries & commands by requiring 0 line of code from platform teams
 * Be easy to use on both platform and consumer side
 * Handle errors
 * Uniformize the communication
 * Be extensible in order to allow customization and extension/addition of new features.

-------
Queries
-------
A query is submitted using à GET request, the parameters will be in the query string not in the body. 
This was the prefered way because we want to keep queries as simple as possible and we also think that using GET 
is handy with tools such as curl. Of course it imposes restrictions on having flat/simple queries and limited query size. 
We will address those points if they become really required.

To enable Query exposition register HttpQueryExposer servlet, it will then use the IQueryServicesLocator to locate each query service.

Ex: suppose we have the following query, it will be **available at host:port/someRootPath/getMemberMessages?memberId=999**.

.. code-block:: java

  class GetMemberMessagesQuery implements IQuery {
     int memberId;
     Date startingFrom;

     // getters & setters
  }

Query objects will be flattened by the framework to a query string, you should **avoid having complex structures**. 
The framework will use the getters and setters during serialization/deserialization. 
The framework also **supports deserialization to objects that don't have a default no arg constructor** (yay!) another handy feature :)

We might also add later support of ser/deser based on fields (being able to mix methods and fields or juste use one or another).


In case of an error a standard HTTP error code will be set with the reason for this error 
(you have it in the response body as json and in the headers).

.. code-block:: json

   {
     "code": 404,
     "reason": "Some query was not found..."
   }

TypeAdapters
++++++++++++
Internally Kasper exposition layer uses what we call TypeAdapters, they allow to work parse/build queries from java types. 
By default we provide a set of such adapters for most common types (primitives, dates, etc). 
But you might need to define a custom TypeAdapter for types we do not handle yet (or just open an issue if it is a standard type so we add it).

Suppose you want to support URIs but there is no default adapter for this type:

.. code-block:: java

  class URITypeAdapter implements ITypeAdapter<URI> {
    @Override
    public void adapt(URI value, QueryBuilder builder) {
        builder.add(value.toString());
    }

    @Override
    public URI adapt(QueryParser parser) throws Exception {
	// consume current uri value (will not be available anymore in the parser
        return new URI(parser.value());
    }
  }

To make your TypeAdapter automatically discovered you can use Java service loader mechanism. Just **create a file named
com.viadeo.kasper.query.exposition.ITypeAdatper in META-INF/services (must be exported in the final jar)** 
and write the full name of each custom typeadapter (one per line) com.viadeo.somepackage.URITypeAdapter. 
The framework will automatically detect it, this is the standard java mechanism used in order to provide spi 
mechanisms for JSR implementors.


The framework will also handle null & missing values for you. 
During serialization you will never be called with a null value, and during deserialization you are sure that there is an actual value.


Complex Queries & BeanAdapters
++++++++++++++++++++++++++++++
If you need to support some complex query, we provide a way to do so by using custom BeanAdapters. 
Consider you want to have some kind of filtering.

.. code-block:: java

  class SomeQuery implements IQuery {
    List<Filter> filters;
    String someField;
  }

  class Filter {
    String key;
    String value;
  }

Filter is not a standard type, but a POJO, we could handle it too, but it would encourage having complex queries. 
To support it you will have to create a custom BeanAdapter.

.. code-block:: java

  class ListOfFilterAdapter extends BeanAdapter<List<Filter>> {
    @Override
    public void adapt(List<Filter> filters, QueryBuilder builder, BeanProperty property) {
      for (Filter filter : filters) {
         builder.addSingle(property.getName()+"_"+filter.key, filter.value);
      }
    }

    @Override
    public List<Filter> adapt(QueryParser parser, BeanProperty property) {
      final String prefix = property.getName() + "_";
      final List<Filter> list = new ArrayList<Filter>();
      for (String name : parser.names()) {
         if (name.startsWith(prefix)) {
            parser.begin(name);
            list.add(new Filter(name.replace(prefix, ""), parser.value()));
            parser.end();
         }
      }
      
      return list;
    }
  }

Then to register it, use the same mechanism as for TypeAdapters, the only difference here is that you must 
put your adapter into a file named com.viadeo.kasper.query.exposition.BeanAdapter.
