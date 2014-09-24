// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.results;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.List;

@XKasperQueryResult(description = "the group authorization info")
public class GroupResult implements QueryResult {

    private final String name;
    private final List<UserResult> users;
    private final KasperID kasperId;
    private final List<RoleResult> roles;
    private final List<PermissionResult> permissions;


    public GroupResult(final KasperID kasperId, final String name, final List<UserResult> users, final List<RoleResult> roles, final List<PermissionResult> permissions) {
        this.name = name;
        this.users = users;
        this.kasperId = kasperId;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<UserResult> getUsers() {
        return users;
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public List<RoleResult> getRoles() {
        return roles;
    }

    public List<PermissionResult> getPermissions() {
        return permissions;
    }

    public static GroupResult getGroupResult(final Group group) {
        List<UserResult> users = Lists.newArrayList();
        for (User user : group.getUsers()) {
            users.add(UserResult.getUserResult(user));
        }
        List<RoleResult> roles = Lists.newArrayList();
        for (Role role : group.getRoles()) {
            roles.add(RoleResult.getRoleResult(role));
        }
        List<PermissionResult> permissions = Lists.newArrayList();
        for (WildcardPermission permission : group.getPermissions()) {
            permissions.add(PermissionResult.getPermissionResult(permission));
        }
        return new GroupResult(group.getEntityId(), group.getName(), users, roles, permissions);
    }


    public static List<GroupResult> getGroupResults(final List<Group> groups) {
        List<GroupResult> groupResults =  Lists.newArrayList();
        for(Group group : groups){
            groupResults.add(getGroupResult(group));
        }
        return groupResults;
    }
}
