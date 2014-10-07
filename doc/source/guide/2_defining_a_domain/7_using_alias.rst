
Using alias
========================

Kasper offers possibility to define aliases for Command, Query and Event. This functionality can be useful in order to keep
the retro-compatibility particularly in the case where we change the name of a command, a query or an event.

To define aliases is really easy you have just to add `XKasperAlias` annotation in specifying which alias you want to
attribute on the class definition of input.

It is really easy to define aliases you have just to add `XKasperAlias` annotation on top of the wanted class and to specify one or several alias that you want to
attribute to your input.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.annotation.XKasperAlias;

    @XKasperAlias(values = {"CreateItem", "CreateSomething"})
    public class CreateStuffCommand implements Command {
        ...
    }