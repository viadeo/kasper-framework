// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.storage;

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

    Actor getActor(Context context);

    /********************************* Backoffice methods ***********************************/

    WildcardPermission createPermission(WildcardPermission permission);

    void deletePermission(WildcardPermission permission);

    Role createRole(Role role);

    void deleteRole(Role role);

    User createUser(User user);

    void deleteUser(User user);

    Group createGroup(Group group);

    void deleteGroup(Group group);

    /****************************** add/remove **********************************/

    Role addPermissionToRole(Role_has_Permission role_has_permission);

    Role removePermissionFromRole(Role_has_Permission role_has_permission);

    User addPermissionToUser(User_has_Permission user_has_Permission);

    User removePermissionFromUser(User_has_Permission user_has_Permission);

    Group addPermissionToGroup(Group_has_Permission group_has_permission);

    Group removePermissionFromGroup(Group_has_Permission group_has_permission);

    User addRoleToUser(User_has_Role user_has_role);

    User removeRoleFromUser(User_has_Role user_has_role);

    Group addRoleToGroup(Group_has_Role group_has_role);

    Group removeRoleFromGroup(Group_has_Role group_has_role);

    Group addUserToGroup(Group_has_User group_has_user);

    Group removeUserFromGroup(Group_has_User group_has_user);

    /************************************* gets ****************************/

    WildcardPermission getPermission(KasperID kasperID);

    List<WildcardPermission> getAllPermissions();

    Role getRole(KasperID kasperID);

    List<Role> getAllRoles();

    Group getGroup(KasperID kasperID);

    List<Group> getAllGroups();

    User getUser(KasperID kasperID);

    List<User> getAllUsers();

    List<Role> getRolesHavingPermission(KasperID permissionId);

    List<Group> getGroupsHavingRole(KasperID roleId);

    List<Group> getGroupsHavingPermission(KasperID permissionId);

    List<Group> getGroupsHavingUser(KasperID userId);

    List<User> getUsersHavingRole(KasperID roleId);

    List<User> getUsersHavingPermission(KasperID permissionId);

}
