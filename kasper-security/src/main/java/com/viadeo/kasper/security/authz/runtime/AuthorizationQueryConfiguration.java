// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.runtime;

import com.viadeo.kasper.security.authz.queries.handlers.*;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

public class AuthorizationQueryConfiguration {

    public GetGroupQueryHandler getGroupQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupQueryHandler(authorizationStorage);
    }

    public GetGroupsHavingPermissionQueryHandler getGroupsHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsHavingPermissionQueryHandler(authorizationStorage);
    }

    public GetGroupsHavingRoleQueryHandler getGroupsHavingRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsHavingRoleQueryHandler(authorizationStorage);
    }

    public GetGroupsHavingUserQueryHandler getGroupsHavingUserQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsHavingUserQueryHandler(authorizationStorage);
    }

    public GetGroupsQueryHandler getGroupsQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetGroupsQueryHandler(authorizationStorage);
    }

    public GetRoleQueryHandler getRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetRoleQueryHandler(authorizationStorage);
    }

    public GetRolesHavingPermissionQueryHandler getRolesHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetRolesHavingPermissionQueryHandler(authorizationStorage);
    }

    public GetRolesQueryHandler getRolesQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetRolesQueryHandler(authorizationStorage);
    }

    public GetUserQueryHandler getUserQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUserQueryHandler(authorizationStorage);
    }

    public GetUsersHavingPermissionQueryHandler getUsersHavingPermissionQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUsersHavingPermissionQueryHandler(authorizationStorage);
    }

    public GetUsersHavingRoleQueryHandler getUsersHavingRoleQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUsersHavingRoleQueryHandler(authorizationStorage);
    }

    public GetUsersQueryHandler getUsersQueryHandler(AuthorizationStorage authorizationStorage) {
        return new GetUsersQueryHandler(authorizationStorage);
    }

}
