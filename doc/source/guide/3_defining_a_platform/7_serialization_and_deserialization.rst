
Serialization and Deserialization
========================

By default Kasper provide a pre-configured ObjectMapper ready to work. We can retrieve this mapper in invoking `ObjectMapperProvider.INSTANCE.mapper()`

However according to your case then you can specialize to register new module on the mapper.


TypeAdapters
-----------------------

Internally Kasper exposition layer uses what we call *TypeAdapters*, they allow to work parse/build queries from java types.

By default we provide a set of such adapters for most common types (primitives, dates, etc).

But you might need to define a **custom TypeAdapter** for types we do not handle yet (or just open an issue if it is a standard type so we'll add it).

Suppose you want to support URIs but there is no default adapter for this type:

.. code-block:: java
    :linenos:

    class URITypeAdapter implements TypeAdapter<URI> {
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

To make your TypeAdapter automatically discovered you can use `Java service loader mechanism <http://docs.oracle.com/javase/tutorial/ext/basics/spi.html#register-service-providers>`_.
Just create a file named **com.viadeo.kasper.query.exposition.TypeAdapter** in **META-INF/services** (*must be exported in the final jar*)
and write the full name of each custom TypeAdapter (one per line) ::

    com.viadeo.somepackage.URITypeAdapter

The framework will automatically detect it, this is the standard java mechanism used in order to provide spi
mechanisms for JSR implementors.

The framework will also handle null & missing values for you.
During serialization you will never be called with a null value, and during deserialization you are sure that there is an actual value.


Complex Queries & BeanAdapters
-----------------------

If you need to support some complex query using **GET**, we provide a way to do so by using custom BeanAdapters.

Consider you want to have some kind of filtering.

.. code-block:: java
    :linenos:

    class SomeQuery implements Query {
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
  :linenos:

  class ListOfFilterAdapter implements BeanAdapter<List<Filter>> {

    @Override
    public void adapt(final List<Filter> filters, final QueryBuilder builder, final BeanProperty property) {
      for (final Filter filter : filters) {
         builder.addSingle(property.getName()+"_"+filter.key, filter.value);
      }
    }

    @Override
    public List<Filter> adapt(final QueryParser parser, final BeanProperty property) {
      final String prefix = property.getName() + "_";
      final List<Filter> list = new ArrayList<Filter>();

      for (final String name : parser.names()) {
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
put your adapter into a file named **com.viadeo.kasper.query.exposition.query.BeanAdapter**.

..  _Error_codes: