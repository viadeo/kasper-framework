
General consideration
========================

..  _Naming_practices:

Naming practices
--------------------------------

It is heavily recommended to name your components using an explicit suffix :

- ServiceAdapter
- [Query]Handler
- [Domain]Service
- [Command]Handler
- [Event]Listener
- Repository
- Command
- Event
- Query
- Domain
- ...

Except for domain elements like **concepts** where it is more readable to directly use the ubiquitous language
names *(eg. User, Member, Group, ...)* or **relations** where the Kasper convention *<Concept>_<verb>_<Concept>*
is encouraged *(eg. Member_isConnectedTo_Member, ..)*.

Concerning Commands/Handlers, Queries/Handlers, Responses and Events/Listeners the rule is **Intention Revealing Interfaces**,
reflecting directly your ubiquitous language elements, eg:

- SetMemberAsPremiumForOneYearCommand / SetMemberAsPremiumForOneYearCommandHandler
- FindNameOfMembersFromIdsQuery / FindNameOfMembersFromIdsQueryHandler
- ListOfMembersWithNamesResponse
- MemberHasBeenSetAsPremiumForOneYearEvent / MemberHasBeenSetAsPremiumForOneYearEventListener

instead of (**the following are bad practices**) :

- UpdateMemberCommand / ChangeMemberPropertiesHandler
- GetMembersQuery / GetMembersService
- MembersDTO
- MemberUpdateEvent

Do not miss that **you will not be the only person to handle your objects** : they are auto-exposed, auto-documented,
your events will be listened by other domains, some subsystems, etc..


..  _Packages_names:

Packages names
--------------------------------

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

