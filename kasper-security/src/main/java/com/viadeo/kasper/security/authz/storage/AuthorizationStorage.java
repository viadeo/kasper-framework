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

    public void createPermission(final WildcardPermission permission);

    public void deletePermission(final WildcardPermission permission);

    public Role createRole(final Role role);

    public void deleteRole(final Role role);

    public User createUser(final User user);

    public void deleteUser(final User user);

    public Group createGroup(final Group group);

    public void deleteGroup(final Group group);

    /****************************** add/remove **********************************/

    public Role addPermissionToRole(final Role_has_Permission role_has_permission);

    public Role removePermissionFromRole(final Role_has_Permission role_has_permission);

    public User addPermissionToUser(final User_has_Permission user_has_Permission);

    public User removePermissionFromUser(final User_has_Permission user_has_Permission);

    public Group addPermissionToGroup(final Group_has_Permission group_has_permission);

    public Group removePermissionFromGroup(final Group_has_Permission group_has_permission);

    public User addRoleToUser(final User_has_Role user_has_role);

    public User removeRoleFromUser(final User_has_Role user_has_role);

    public Group addRoleToGroup(final Group_has_Role group_has_role);

    public Group removeRoleFromGroup(final Group_has_Role group_has_role);

    public Group addUserToGroup(final Group_has_User group_has_user);

    public Group removeUserFromGroup(final Group_has_User group_has_user);

    /************************************* gets ****************************/

    public WildcardPermission getPermission(final KasperID kasperID);

    public List<Permission> getAllPermissions();

    public Role getRole(final KasperID kasperID);

    public List<Role> getAllRoles();

    public Group getGroup(final KasperID kasperID);

    public List<Group> getAllGroups();

    public User getUser(final KasperID kasperID);

    public List<User> getAllUsers();

    public List<Role> getRolesHavingPermission(final KasperID permissionId);

    public List<Group> getGroupsHavingRole(final KasperID roleId);

    public List<Group> getGroupsHavingPermission(final KasperID permissionId);

    public List<Group> getGroupsHavingUser(final KasperID userId);

    public List<User> getUsersHavingRole(final KasperID roleId);

    public List<User> getUsersHavingPermission(final KasperID permissionId);

}
