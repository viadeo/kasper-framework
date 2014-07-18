package com.viadeo.kasper.security.authz.queries.results;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.List;

@XKasperQueryResult(description = "the user authorization info")
public class UserResult implements QueryResult {

    private final String firstName;
    private final String lastName;
    private final KasperID kasperId;
    private final List<RoleResult> roles;
    private final List<PermissionResult> permissions;

    public UserResult(final KasperID kasperId, final String firstName, final String lastName, final List<RoleResult> roles, final List<PermissionResult> permissions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.kasperId = kasperId;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public static UserResult getUserResult(final User user){
        List<RoleResult> roles = Lists.newArrayList();
        List<PermissionResult> permissions = Lists.newArrayList();
        for(Role role : user.getRoles()){
            roles.add(RoleResult.getRoleResult(role));
        }
        for(WildcardPermission permission : user.getPermissions()){
            permissions.add(PermissionResult.getPermissionResult(permission));
        }
        return new UserResult(user.getEntityId(), user.getFirstName(), user.getLastName(), roles, permissions);
    }

    public static List<UserResult> getUserResults(final List<User> users){
        List<UserResult> usersResults = Lists.newArrayList();
        for (User user : users) {
            usersResults.add(getUserResult(user));
        }
        return usersResults;
    }
}
