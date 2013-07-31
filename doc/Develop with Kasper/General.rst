
Kasper: General considerations
==============================

Kasper generally asks you to implements its building blocks :

- implementing one or several interfaces or abstract classes

**and**

- annotating your classes with an **@XKasperXXXX** java annotation

Like one command handler :

.. code-block:: java
    :linenos:

    @XKasperCommandHandler( domain = MyDomain.class )
    public class DoSomethingCommandHandler implements CommandHandler<DoSomethingCommand> {
        public Object handle(final CommandMessage<DoSomethingCommand> message, UnitOfWork uow) {
            ...
        }
    }

Kasper generally also provides you some abstract classes in order to ease your day-to-day work :

.. code-block:: java
    :linenos:

    @XKasperCommandHandler( domain = MyDomain.class )
    public class DoSomethingCommandHandler extends AbstractCommandHandler<DoSomethingCommand> {
        public CommandResult handle(final DoSomethingCommand command) {
            ...
        }
    }

The following annotations are currently used by the framework :

- **XKasperEventListener**
- **XKasperServiceFilter**
- **XKasperQueryService**
- **XKasperCommandHandler**
- **XKasperRepository**
- **XKasperRelation**
- **XKasperConcept**
- XKasperCommand
- XKasperDomain
- XKasperEvent

Some are mandatory (**here in bold**) for discovery and registering, other are optional, for adding metadata to your components.

You can create unregistered components adding the **@XKasperUnregistered** annotation.

