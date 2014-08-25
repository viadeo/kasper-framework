// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.storage;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.entities.relations.*;

import java.util.List;

public interface AuthorizationStorage {

    Optional<Actor> getActor(Context context);

    /********************************* Backoffice methods ***********************************/

    boolean createPermission(WildcardPermission permission);

    boolean deletePermission(WildcardPermission permission);

    boolean createRole(Role role);

    boolean deleteRole(Role role);

    boolean createUser(User user);

    boolean deleteUser(User user);

    boolean createGroup(Group group);

    boolean deleteGroup(Group group);

    /****************************** add/remove **********************************/

    Optional<Role> addPermissionToRole(Role_has_Permission role_has_permission);

    Optional<Role> removePermissionFromRole(Role_has_Permission role_has_permission);

    Optional<User> addPermissionToUser(User_has_Permission user_has_Permission);

    Optional<User> removePermissionFromUser(User_has_Permission user_has_Permission);

    Optional<Group> addPermissionToGroup(Group_has_Permission group_has_permission);

    Optional<Group> removePermissionFromGroup(Group_has_Permission group_has_permission);

    Optional<User> addRoleToUser(User_has_Role user_has_role);

    Optional<User> removeRoleFromUser(User_has_Role user_has_role);

    Optional<Group> addRoleToGroup(Group_has_Role group_has_role);

    Optional<Group> removeRoleFromGroup(Group_has_Role group_has_role);

    Optional<Group> addUserToGroup(Group_has_User group_has_user);

    Optional<Group> removeUserFromGroup(Group_has_User group_has_user);

    /************************************* gets ****************************/

    Optional<WildcardPermission> getPermission(KasperID kasperID);

    Optional<List<WildcardPermission>> getAllPermissions();

    Optional<Role> getRole(KasperID kasperID);

    Optional<List<Role>> getAllRoles();

    Optional<Group> getGroup(KasperID kasperID);

    Optional<List<Group>> getAllGroups();

    Optional<User> getUser(KasperID kasperID);

    Optional<List<User>> getAllUsers();

    Optional<List<Role>> getRolesHavingPermission(KasperID permissionId);

    Optional<List<Group>> getGroupsHavingRole(KasperID roleId);

    Optional<List<Group>> getGroupsHavingPermission(KasperID permissionId);

    Optional<List<Group>> getGroupsHavingUser(KasperID userId);

    Optional<List<User>> getUsersHavingRole(KasperID roleId);

    Optional<List<User>> getUsersHavingPermission(KasperID permissionId);

}
