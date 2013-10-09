
Validation
==========

Kasper provides Command and Query validation through `JSR 303 Bean Validation <http://beanvalidation.org/1.0/spec/>`_.
To enable bean validation just add to your classpath an implementation like hibernate-validator and you are ready to go!

The framework will take care of everything, it will ensure that the commands & queries are valid before submitting it to your
command handler or query service. If they are not valid, the framework will constructor a QueryResult error, with the list of
violations as messages.


.. code-block:: java
    :linenos:

    public class Person {
        @Min(18) private int age;
        @Valid @NotNull private Address address;

        ...
    }

    public class Address {
        @NotNull private String street;

        ...
    }

**Note** the use of @Valid annotation on address property. This will recursively validate the properties of address object.