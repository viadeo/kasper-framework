
Commands and Queries Validation
===============================

Kasper provides Command and Query validation through `JSR 303 Bean Validation <http://beanvalidation.org/1.0/spec/>`_.

To enable bean validation just add to your classpath an implementation like `hibernate-validator <http://www.hibernate.org/subprojects/validator.html>`_ and you are ready to go!

The framework will take care of everything, it will ensure that the commands & queries are valid before submitting it to your
command handler or query handler. If they are not valid, the framework will constructor a QueryResponse error, with the list of
violations as error messages.

.. code-block:: java
    :linenos:

    public class CreateANewPersonCommand implements Command {
        @Min(18) private int age;
        @Valid @NotNull private Address address;
        ...
    }

    public class AddressQueryResult implements QueryResult {
        @NotNull private String street;
        ...
    }

**Note** the use of @Valid annotation on address property. This will recursively validate the properties of address object.
