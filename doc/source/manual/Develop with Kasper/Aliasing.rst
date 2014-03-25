
Commands and Queries Aliasing
===============================

Kasper offers possibility to define aliases for Command and Query. This functionality can be useful in order to keep
retro-compatibility. Particularly in the case where we change the name of a command or a query.

To define aliases for Command or/and a Query just add `XKasperAlias` annotation in specifying which alias you want to
attribute for the input object.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.annotation.XKasperAlias;

    @XKasperAlias(values = {"CreateItem", "CreateSomething"})
    public class CreateStuffCommand implements Command {
        ...
    }
