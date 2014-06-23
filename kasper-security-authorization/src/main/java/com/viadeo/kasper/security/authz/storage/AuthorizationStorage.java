package com.viadeo.kasper.security.authz.storage;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.*;

import java.util.List;

public interface AuthorizationStorage {

    Actor getActor(Context context);

    /********************************* Backoffice methods ***********************************/

    public void createPermission(final Permission permission);

    public void deletePermission(final Permission kasperID);

    public Role createRole(final Role role);

    public void deleteRole(final Role kasperID);

    public User createUser(final User user);

    public void deleteUser(final User user);

    public Group createGroup(final Group group);

    public void deleteGroup(final Group group);

    /****************************** add/remove **********************************/

    public Role addPermissionToRole(final Role_has_Permission role_has_permission);

    public Role removePermissionFromRole(final Role_has_Permission role_has_permission);

    public void addPermissionToUser(final User_has_Permission user_has_Permission);

    public void removePermissionFromUser(final User_has_Permission user_has_Permission);

    public void addPermissionToGroup(final Group_has_Permission group_has_permission);

    public void removePermissionFromGroup(final Group_has_Permission group_has_permission);

    public void addRoleToUser(final User_has_Role user_has_role);

    public void removeRoleFromUser(final User_has_Role user_has_role);

    public Group addRoleToGroup(final Group_has_Role group_has_role);

    public void removeRoleFromGroup(final Group_has_Role group_has_role);

    public void addUserToGroup(final Group_has_User group_has_user);

    public void removeUserFromGroup(final Group_has_User group_has_user);

    /************************************* gets ****************************/

    public WildcardPermission getPermission(final KasperID kasperID);

    public List<Permission> getAllPermissions();

    public Role getRole(final KasperID kasperID);

    public List<Role> getAllRoles();

    public Group getGroup(final KasperID kasperID);

    public List<Group> getAllGroups();

    public User getUser(final KasperID kasperID);

    public List<User> getAllUsers();

    public List<User> getUsersForGroup(final Group group);

    public List<Permission> getPermissionsForRole(final Role role);

    public List<Permission> getPermissionsForUser(final User user);

    public List<Permission> getPermissionsForGroup(final Group group);

    public List<Role> getRolesForGroup(final Group group);

    public List<Role> getRolesForUser(final User user);

    public List<Group> getAllGroupsForRole(final Role role);

    public List<User> getAllUsersForRole(final Role role);

}
