
Using auto documentation
========================


..  _Installing_auto_documentation:

Installing auto documentation
------------------------

The auto documentation is available as a plugin thanks to `DocumentationPlugin` plugin which defines some web services.

From the plugin, you can retrieve Jersey resource `KasperDocResource`.

.. code-block:: java
    :linenos:

    ObjectMapper objectMapper = ObjectMapperProvider.INSTANCE.mapper()

    DocumentationPlugin documentationPlugin = new DocumentationPlugin();

    Platform platform = new Platform.Builder()
        .addPlugin(documentationPlugin)
        .build()

    KasperDocResource docResource = documentationPlugin.getKasperDocResource();


..  _Accessing documentation:

Accessing documentation
------------------------

We can access to the documentation under two different ways :

- via user interface
- via web services

**User interface**

The UI is a client of bellow web services.

*usage :*
::
    # OSX
    open http://internal-kasper.demo.sf.viadeo.internal/doc/

    # Linux
    xdg-open http://internal-kasper.demo.sf.viadeo.internal/doc/


**web services**

+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------------------------------------------------------------------------------+
| Url                                                                                                                                                                                                                             | Description                                                                     |
+=================================================================================================================================================================================================================================+=================================================================================+
| <host_name>/kasper/doc/domains                                                                                                                                                                                                  | List all domains with its components in light version                           |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------------------------------------------------------------------------------+
| <host_name>/kasper/doc/domain/<domain_name>                                                                                                                                                                                     | Get a specific domain with its components in light version                      |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------------------------------------------------------------------------------+
| <host_name>/kasper/doc/domain/<domain_name>/<'commands' or 'queries or events' or 'queryResults' or 'commandHandlers' or 'queryHandlers' or 'eventListeners' or 'concepts' or 'relations' or 'repositories'>                    | List components in full version by type of a specific domain                    |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------------------------------------------------------------------------------+
| <host_name>/kasper/doc/domain/<domain_name>/<'commands' or 'queries' or 'events' or 'queryResults' or 'commandHandlers' or 'queryHandlers' or 'eventListeners' or 'concepts' or 'relations' or 'repositories'>/<component_name> | Get a specific component in full version of a specific domain                   |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------------------------------------------------------------------------------+

*usage :*
::
    curl http://internal-kasper.demo.sf.viadeo.internal/kasper/doc/domains

    curl http://internal-kasper.demo.sf.viadeo.internal/kasper/doc/domain/Subscription/command/FreeSubscriptionByAffiliateCommand


Completing documentation
------------------------

**`XKasperEvent` annotation**

+-----------------+--------------------------------------------------------------+
| Property        |  Description                                                 |
+=================+==============================================================+
|  prefix         | the domain's prefix                                          |
+-----------------+--------------------------------------------------------------+
|  label          | the domain's label                                           |
+-----------------+--------------------------------------------------------------+
|  description    | the domain's description                                     |
+-----------------+--------------------------------------------------------------+
|  owner          | the domain's owner                                           |
+-----------------+--------------------------------------------------------------+


**`XKasperEvent` annotation**

+-----------------+--------------------------------------------------------------+
| Property        |  Description                                                 |
+=================+==============================================================+
|  action         | the event's action                                           |
+-----------------+--------------------------------------------------------------+
|  description    | the event's description                                      |
+-----------------+--------------------------------------------------------------+


**`XKasperCommand` and `XKasperQuery` annotation**

+-----------------+--------------------------------------------------------------+
| Property        |  Description                                                 |
+=================+==============================================================+
|  description    | the input's description                                      |
+-----------------+--------------------------------------------------------------+


**`XKasperCommandHandler` and `XKasperEventListener` annotation**

+-----------------+--------------------------------------------------------------+
| Property        |  Description                                                 |
+=================+==============================================================+
|  domain         | the domain of this event listener                            |
+-----------------+--------------------------------------------------------------+
|  description    | the event listener's description                             |
+-----------------+--------------------------------------------------------------+


**`XKasperQueryHandler` annotation**

+-----------------+--------------------------------------------------------------+
| Property        |  Description                                                 |
+=================+==============================================================+
|  name           | the name of the handler                                      |
+-----------------+--------------------------------------------------------------+
|  domain         | the domain of this event listener                            |
+-----------------+--------------------------------------------------------------+
|  description    | the event listener's description                             |
+-----------------+--------------------------------------------------------------+


**`XKasperField` annotation**

This annotation must be used on field declaration of command, query, event or result. It allows to provide a detailed
description.

+-----------------+--------------------------------------------------------------+
| Property        |  Description                                                 |
+=================+==============================================================+
|  description    | a description on a specific field                            |
+-----------------+--------------------------------------------------------------+


**`XKasperPublic` annotation**

This annotation must be used on command or query handler class definition. It allows to identify which 'resources' are
public or not.


**`XKasperAlias` annotation (optional)**

This annotation must be used on command, query  declaration of command, query, event or result. It allows to list aliases
for each input (command|query|event)

**`XKasperRequirePermissions` annotation**

This annotation must be used on command or query handler class definition. It restricts the access to User having required permission.

**`XKasperRequireRoles` annotation**

This annotation must be used on command or query handler class definition. It restricts the access to User having required role.