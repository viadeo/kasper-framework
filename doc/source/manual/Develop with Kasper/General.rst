
Kasper: General considerations
==============================

Kasper components implementation
--------------------------------

Kasper generally asks you to implements its building blocks :

- implementing one or several interfaces or abstract classes

**and**

- optionally annotating your classes with an **@XKasperXXXX** java annotation

Like one command handler :

.. code-block:: java
    :linenos:

    @XKasperCommandHandler( domain = MyDomain.class )
    public class DoSomethingCommandHandler extends CommandHandler<DoSomethingCommand> {
        public Object handle(final DoSomethingCommand command) {
            ...
        }
    }

Kasper generally also provides you some abstract classes in order to ease your day-to-day work :

.. code-block:: java
    :linenos:

    @XKasperCommandHandler( domain = MyDomain.class )
    public class DoSomethingCommandHandler extends CommandHandler<DoSomethingCommand> {
        public CommandResponse handle(final DoSomethingCommand command) {
            ...
        }
    }

Available Kasper annotations
----------------------------

The following annotations are currently used by the framework :

- **XKasperEventListener**
- **XKasperServiceAdapter**
- **XKasperQueryHandler**
- **XKasperCommandHandler**
- **XKasperRepository**
- **XKasperRelation**
- **XKasperConcept**
- XKasperCommand
- XKasperDomain
- XKasperEvent
- XKasperQuery
- XKasperPublic
- XKasperRequirePermissions
- XKasperRequireRoles

Some are mandatory (**here in bold**) for discovery and registering, other are optional, for adding metadata to your components.

You can create unregistered components adding the **@XKasperUnregistered** annotation.

Naming practices
----------------

It is heavily recommended to name your components using an explicit suffix :

- ServiceAdapter
- QueryHandler
- DomainService
- [Command]Handler
- [Event]Listener
- Repository
- Command
- Event
- Query
- Domain

Except for domain elements like **concepts** where it is more readable to directly use the ubiquitous language
names *(eg. User, Member, Group, ...)* or **relations** where the Kasper convention *<Concept>_<verb>_<Concept>*
is encouraged *(eg. Member_isConnectedTo_Member, ..)*.

Concerning Commands/Handlers, Queries/Handlers, Responses and Events/Listeners the rule is **Intention Revealing Interfaces**,
reflecting directly your ubiquitous language elements, eg:

- SetMemberAsPremiumForOneYear*Command* / SetMemberAsPremiumForOneYear*CommandHandler*
- FindNameOfMembersFromIds*Query* / FindNameOfMembersFromIds*QueryHandler*
- ListOfMembersWithNames*Response*
- MemberHasBeenSetAsPremiumForOneYear*Event* / MemberHasBeenSetAsPremiumForOneYear*EventListener*

instead of (**the following are bad practices**) :

- UpdateMember*Command* / ChangeMemberProperties*Handler*
- GetMembers*Query* / GetMembers*Service*
- MembersDTO
- MemberUpdate*Event*

Do not miss that **you will not be the only person to handle with your objects** : they are auto-exposed, auto-documented,
your events will be listened by other domains, some subsystems, the business intelligence datawarehouse, etc..

Packages names
--------------

Considering a root package called **com.viadeo.platform** (choose yours..), we recommend you follow the following
packages hierarchy :

*<base package>* . **<domain>** . **[<area>]** . **[<type>]** . **<sub-packages>**

+----------------------------------+----------+---------------+--------------------------------------------------------+
|                                  |   Type   |       Area    |   Package prefix                                       |
+==================================+==========+===============+========================================================+
| Domains                          |   API    |    ALL        |  *com.viadeo.platform*.<domain>                        |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Commands                         |   API    |    COMMAND    |  *com.viadeo.platform*.<domain>.command.api.           |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Queries                          |   API    |    QUERY      |  *com.viadeo.platform*.<domain>.query.api.             |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Query results                    |   API    |    QUERY      |  *com.viadeo.platform*.<domain>.query.api.             |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Events                           |   API    |    NONE       |  *com.viadeo.platform*.<domain>.event.api.             |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Command handlers                 |   FLOW   |    COMMAND    |  *com.viadeo.platform*.<domain>.command.               |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Query services                   |   FLOW   |    QUERY      |  *com.viadeo.platform*.<domain>.query.                 |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Query adapters                   |   FLOW   |    QUERY      |  *com.viadeo.platform*.<domain>.query.                 |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Query indexers                   | FLOW/DATA|    QUERY      |  *com.viadeo.platform*.<domain>.query.index.           |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Event listeners                  |   FLOW   |    ALL        |  *com.viadeo.platform*.<domain>.<area>.event.          |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Entities (Concepts & Relations)  |   MODEL  |    COMMAND    |  *com.viadeo.platform*.<domain>.command.model.         |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Domain services                  |   MODEL  |    COMMAND    |  *com.viadeo.platform*.<domain>.command.model.service. |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| Repositories                     |   DATA   |    COMMAND    |  *com.viadeo.platform*.<domain>.command.data.          |
+----------------------------------+----------+---------------+--------------------------------------------------------+
| MyBatis mappers                  |   DATA   |    ALL        |  *com.viadeo.platform*.<domain>.<area>.data.mapper     |
+----------------------------------+----------+---------------+--------------------------------------------------------+




