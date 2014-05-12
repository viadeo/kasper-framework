
Commands and Queries Authorizations
===============================

Kasper provides Command and Query authorizations inspired from `Apache Shiro <https://shiro.apache.org/authorization.html>`_.

There are two types of authorizations : Roles and Permissions.

Permissions
-------

Permissions represent what action is needed to access Resource.

Some examples of permission statements :
- delete
- write
- read

To define permissions for Command or/and a Query just add `XKaspeRequirePermissions` annotation in specifying which permissions you want to
check for the resource.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.security.annotation.XKaspeRequirePermissions;

    @XKaspeRequirePermissions(values = {"delete", "write"})
    public class CreateStuffCommand implements Command {
        ...
    }

Permissions are based on `Apache Shiro Wildcard Permissions <https://shiro.apache.org/authorization.html>`_.


Roles
-------

A Role represents a list of permissions. For example, an administrator Role can have delete, read and write permissions.

To define roles for Command or/and a Query just add `XKaspeRequireRoless` annotation in specifying which roles you want to
check for the resource.

.. code-block:: java
    :linenos:

    import com.viadeo.kasper.security.annotation.XKaspeRequireRoles;

    @XKaspeRequireRoles(values = {"admin", "reader"})
    public class CreateStuffCommand implements Command {
        ...
    }

