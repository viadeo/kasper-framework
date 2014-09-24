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
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;

import java.util.List;

@XKasperQueryResult(description = "the role authorization info")
public class RoleResult implements QueryResult {

    private KasperID kasperId;
    private String name;
    private List<PermissionResult> permissions;

    public RoleResult(KasperID kasperId, String name, List<PermissionResult> permissions) {
        this.kasperId = kasperId;
        this.name = name;
        this.permissions = permissions;
    }

    public RoleResult(final Role role) {
        this.kasperId = role.getEntityId();
        this.name = role.getName();
        this.permissions = getPermissionResultsForRole(role);
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public List<PermissionResult> getPermissions() {
        return permissions;
    }

    public String getName() {
        return name;
    }

    public static RoleResult getRoleResult(final Role role) {
        return new RoleResult(role.getEntityId(), role.getName(), getPermissionResultsForRole(role));
    }

    private static List<PermissionResult> getPermissionResultsForRole(final Role role) {
        List<PermissionResult> permissions = Lists.newArrayList();
        for (WildcardPermission permission : role.getPermissions()) {
            permissions.add(new PermissionResult(permission.getEntityId(), permission.toString()));
        }
        return permissions;
    }

    public static List<RoleResult> getRoleResults(final List<Role> roles) {
        List<RoleResult> rolesResults = Lists.newArrayList();
        for(Role role : roles){
            rolesResults.add(new RoleResult(role));
        }
        return rolesResults;
    }
}
